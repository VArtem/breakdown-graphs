import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Simulation3Graphs {

    public static final int k1 = 150;
    public static final int k2 = 150;
    public static final int k3 = 150;

    public static final int n = 1000;
    public static final int MAX_ITERS = 5_000;
    public static final int MAX_COMPONENTS = 100;

    public static final String RUN_FOLDER = "data/runs";

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        runSimulation();
        System.err.println("Finished in " + (System.currentTimeMillis() - time) + " ms");
    }

    private static void runSimulation() {
        Map<Long, List<BreakdownGraph.Edge>> components = new HashMap<>();
        Map<Long, Integer> count = new HashMap<>();
        runIterations(components, count);
        printResults(components, count);
    }

    private static void runIterations(Map<Long, List<BreakdownGraph.Edge>> components, Map<Long, Integer> count) {
        BreakdownGraph graph = new BreakdownGraph(n);
        for (int ITERS = 1; ITERS <= MAX_ITERS; ITERS++) {
            graph.reset();
            Random rng = new Random(ITERS);
            for (int i = 0; i < k1; i++) {
                graph.doRandomDCJ(rng);
            }
            BreakdownGraph copy1 = graph.clone();
            BreakdownGraph copy2 = graph.clone();

            for (int i = 0; i < k2; i++) {
                copy1.doRandomDCJ(rng);
            }
            for (int i = 0; i < k3; i++) {
                copy2.doRandomDCJ(rng);
            }

            BreakdownGraph union = new BreakdownGraph(copy1, copy2);
            union.addToMaps(components, count);
            if (ITERS % 100 == 0) {
                System.err.print("ITERS = " + ITERS);
                System.err.println(", Different components: " + count.size());
            }
        }
    }

    private static void printResults(Map<Long, List<BreakdownGraph.Edge>> components, Map<Long, Integer> count) {
        final String outputDataFolder = String.format("%s/%s/", RUN_FOLDER, new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));
        new File(outputDataFolder + "graphs/").mkdirs();

        List<Map.Entry<Long, List<BreakdownGraph.Edge>>> list = new ArrayList<>(components.entrySet());
        list.sort(Comparator.comparing(entry -> -count.get(entry.getKey())));
        list = list.subList(0, Math.min(list.size(), MAX_COMPONENTS));

        System.err.println("Showing top " + MAX_COMPONENTS + " components");
        double[] freq = new double[list.size()];
        Map<Integer, Double> result = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            freq[i] = 1.0 * count.get(list.get(i).getKey()) / MAX_ITERS;
            result.put(i, freq[i]);
            printGraphDot(list.get(i).getValue(), String.format("%s/graphs/%02d.dot", outputDataFolder, i));
            System.err.printf("%02d: edges = %d, freq = %.10f\n", i, list.get(i).getValue().size(), freq[i]);
        }
        printData(freq, outputDataFolder + "summary.txt");
    }

    private static void printGraphDot(List<BreakdownGraph.Edge> edges, String outFile) {
        final String[] colors = new String[]{"black", "red", "blue"};
        try (PrintWriter out = new PrintWriter(outFile)) {
            out.println("graph {");
            for (BreakdownGraph.Edge e : edges) {
                out.printf("%d -- %d [color = %s", e.from, e.to, colors[e.color]);
                if (e.color == 0) {
                    out.print(", weight = 100.0, penwidth = 4");
                } else {
                    out.print(", weight = 0");
                }
                out.println("];");
            }
            out.println("}");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if (edges.size() > 100) {
            return;
        }
        try {
            String command = "circo -Tsvg -O" + outFile + ".svg " + outFile;
            System.err.println(command);
            Process runtime = Runtime.getRuntime().exec(command);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printData(double[] freq, String outFile) {
        try (PrintWriter out = new PrintWriter(outFile)) {
            for (int i = 1; i < freq.length; i++) {
                out.println(i + " " + freq[i]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
