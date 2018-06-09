import java.util.*;

public class BreakdownGraph {

    int n;
    List<Edge> edges;

    public BreakdownGraph(int genes) {
        this.n = 2 * genes;
        this.edges = new ArrayList<>();
        for (int i = 0; i < n; i += 2) {
            edges.add(new Edge(i, i + 1));
        }
    }

    public BreakdownGraph(BreakdownGraph a, BreakdownGraph b) {
        this.n = a.n;
        this.edges = new ArrayList<>();
        for (int i = 0; i < n / 2; i++) {
            edges.add(new Edge(2 * i, 2 * i + 1, 0));
        }
        for (Edge e : a.edges) {
            edges.add(e);
            e.color = 1;
        }
        for (Edge e : b.edges) {
            edges.add(e);
            e.color = 2;
        }
    }

    public void addToSummary(ComponentStatistics statistics) {
        List<Edge>[] graph = new List[n];
        Arrays.setAll(graph, ArrayList::new);
        for (Edge e : edges) {
            graph[e.from].add(e);
            graph[e.to].add(e);
        }
        int[] q = new int[n];
        boolean[] used = new boolean[n];
        for (int start = 0; start < n; start++) {
            if (!used[start]) {
                int head = 0, tail = 0;
                List<Edge> curComp = new ArrayList<>();
                used[start] = true;
                q[tail++] = start;
                while (head < tail) {
                    int cur = q[head++];
                    for (Edge e : graph[cur]) {
                        int to = e.to + e.from - cur;
                        if (!used[to]) {
                            used[to] = true;
                            q[tail++] = to;
                        }
                    }
                }
                for (int i = 0; i < tail; i++) {
                    for (Edge e : graph[q[i]]) {
                        if (e.from == q[i]) {
                            curComp.add(e);
                        }
                    }
                }
                statistics.addComponent(new ConnectedComponent(curComp));
            }
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

    public BreakdownGraph clone() {
        BreakdownGraph copy = new BreakdownGraph(this.n / 2);
        copy.edges.clear();
        for (Edge e : edges) {
            copy.edges.add(new Edge(e.from, e.to, e.color));
        }
        return copy;
    }

    public void reset() {
        for (int i = 0; i < edges.size(); i++) {
            edges.get(i).from = 2 * i;
            edges.get(i).to = 2 * i + 1;
        }
    }

}
