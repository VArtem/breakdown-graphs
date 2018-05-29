import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BreakdownGraph {

    int n;
    List<Edge> edges;

    class Edge {
        int from, to;
        int color;

        public Edge(int from, int to, int color) {
            this.from = from;
            this.to = to;
            this.color = color;
        }

        public Edge(int from, int to) {
            this(from, to, 0);
        }
    }

    public BreakdownGraph(int genes) {
        this.n = 2 * genes;
        this.edges = new ArrayList<>();
        for (int i = 0; i < n; i += 2) {
            edges.add(new Edge(i, i + 1));
        }
    }

    public void doRandomDCJ(Random rng) {
        Edge e1, e2;
        do {
            e1 = edges.get(rng.nextInt(edges.size()));
            e2 = edges.get(rng.nextInt(edges.size()));
        } while (e1 == e2);
        if (rng.nextBoolean()) {
            int tmp = e1.to;
            e1.to = e2.to;
            e2.to = tmp;
        } else {
            int tmp = e1.to;
            e1.to = e2.from;
            e2.from = tmp;
        }
    }

    public int[] cycleDistribution() {
        int[] result = new int[n + 1];
        boolean[] used = new boolean[n];
        int[] next = new int[n];
        for (Edge e : edges) {
            next[e.to] = e.from;
            next[e.from] = e.to;
        }
        for (int i = 0; i < n; i++) {
            if (!used[i]) {
                int cycleSize = 0;
                do {
                    // use exactly one black and red edge
                    cycleSize++;
                    used[i] = true;
                    i = next[i];
                    used[i] = true;
                    i ^= 1;
                } while (!used[i]);
                result[cycleSize]++;
            }
        }
        return result;
    }
}
