import java.io.File;
import java.util.*;

public class Simulation3Graphs {

    public static final int k1 = 150;
    public static final int k2 = 150;
    public static final int k3 = 150;

    public static final int n = 1000;
    public static final int MAX_ITERS = 10000;
    public static final int MAX_COMPONENTS = 300;

    public static final String RUN_FOLDER = "data/runs";

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        runSimulation();
        System.err.println("Finished in " + (System.currentTimeMillis() - time) + " ms");
    }

    private static void runSimulation() {
        ComponentStatistics stats = new ComponentStatistics();
        runIterations(stats);
        printResults(stats);
    }

    private static void runIterations(ComponentStatistics statistics) {
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
            union.addToSummary(statistics);
            if (ITERS % 100 == 0) {
                System.err.print("ITERS = " + ITERS);
                System.err.println(", Different components: " + statistics.size());
            }
        }
    }

    private static void printResults(ComponentStatistics stats) {
        final String outputDataFolder = String.format("%s/%s", RUN_FOLDER, new java.text.SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()));
        new File(outputDataFolder + "/graphs").mkdirs();

        List<Map.Entry<ConnectedComponent, Integer>> list = getTopComponents(stats);
        System.err.println("Showing top " + MAX_COMPONENTS + " components");
        double[] freq = new double[list.size()];
        Map<Integer, Double> result = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<ConnectedComponent, Integer> entry = list.get(i);
            freq[i] = 1.0 * entry.getValue() / MAX_ITERS;
            result.put(i, freq[i]);
            Utils.printGraphDot(entry.getKey(), String.format("%s/graphs/%02d.dot", outputDataFolder, i));
            System.err.printf("%02d: edges = %d, freq = %.10f\n", i, entry.getKey().edges.size(), freq[i]);
        }
        Utils.printFrequencyData(freq, outputDataFolder + "/summary.txt");
    }

    private static List<Map.Entry<ConnectedComponent, Integer>> getTopComponents(ComponentStatistics stats) {
        List<Map.Entry<ConnectedComponent, Integer>> list = new ArrayList<>(stats.count.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
        list = list.subList(0, Math.min(list.size(), MAX_COMPONENTS));
        return list;
    }
}
