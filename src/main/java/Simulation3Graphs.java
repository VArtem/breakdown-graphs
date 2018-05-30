import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Simulation3Graphs {

    public static final int k1 = 200;
    public static final int k2 = 200;
    public static final int k3 = 200;

    public static final int n = 1000;
    public static final int MAX_ITERS = 100_000;

    public static void main(String[] args) {
        long time = System.currentTimeMillis();
        runSimulation();
        System.err.println("Finished in " + (System.currentTimeMillis() - time) + " ms");
    }

    private static void runSimulation() {
        double[] distribution = new double[n + 1];
        for (int ITERS = 1; ITERS <= MAX_ITERS; ITERS++) {
            BreakdownGraph graph = new BreakdownGraph(n);
            Random rng=  new Random(ITERS);
            for (int i = 0; i < k1; i++) {
                graph.doRandomDCJ(rng);
            }
            int[] result = graph.cycleDistribution();
            for (int i = 1; i <= n; i++) {
                distribution[i] += 1.0 * result[i] / MAX_ITERS;
            }
            if (ITERS % 1000 == 0) {
                System.err.println("ITERS = " + ITERS);
            }
        }
        System.err.println(Arrays.toString(distribution));
        Map<Integer, Double> freq = new HashMap<>();
        for (int i = 1; i <= n; i++) {
            freq.put(i, distribution[i]);
        }
        printData(distribution, "graph.txt");
        Histogram h = new Histogram(freq);
        h.launch();
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
