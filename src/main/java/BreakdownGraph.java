import java.util.*;

public class BreakdownGraph {

    public final static int MAX_HASH_ITERS = 10;
    public final static int HASH_BASE = 1_000_003;
    public final static int COMP_HASH_BASE = 424243;

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

    public void addToMaps(Map<Long, List<Edge>> components, Map<Long, Integer> count) {
        List<Edge>[] graph = new List[n];
        Arrays.setAll(graph, ArrayList::new);
        for (Edge e : edges) {
            graph[e.from].add(e);
            graph[e.to].add(e);
        }
        long[] vertexHash = getVertexHashes(graph);
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
                List<Long> curHashes = new ArrayList<>();
                for (int v : Arrays.copyOf(q, tail)) {
                    curHashes.add(vertexHash[v]);
                    for (Edge e : graph[v]) {
                        if (e.from == v) {
                            curComp.add(e);
                        }
                    }
                }
                Collections.sort(curHashes);
                long compHash = 0;
                for (long hash : curHashes) {
                    compHash = compHash * COMP_HASH_BASE + hash;
                }
                compHash = curComp.size();
                components.put(compHash, curComp);
                if (!count.containsKey(compHash)) {
                    count.put(compHash, 1);
                } else {
                    count.put(compHash, count.get(compHash) + 1);
                }
            }
        }
    }

    private long[] getVertexHashes(List<Edge>[] graph) {
        long[] vertexHash = new long[n];
        long[] tmpHash = new long[n];
        Arrays.fill(vertexHash, 1);
        for (int IT = 0; IT < MAX_HASH_ITERS; IT++) {
            for (int i = 0; i < n; i++) {
                List<Item> next = new ArrayList<>();
                for (Edge e : graph[i]) {
                    int to = e.to + e.from - i;
                    next.add(new Item(to, e.color, vertexHash[to]));
                }
                Collections.sort(next);
                tmpHash[i] = 1;
                for (Item item : next) {
                    tmpHash[i] = tmpHash[i] * HASH_BASE + item.hash;
                    tmpHash[i] = tmpHash[i] * HASH_BASE + item.color;
                }
            }
            System.arraycopy(tmpHash, 0, vertexHash, 0, n);
        }
        return vertexHash;
    }

    class Item implements Comparable<Item> {
        int id, color;
        long hash;

        public Item(int id, int color, long hash) {
            this.id = id;
            this.color = color;
            this.hash = hash;
        }


        @Override
        public int compareTo(Item o2) {
            if (this.color != o2.color) {
                return this.color - o2.color;
            }
            return Long.compare(this.hash, o2.hash);
        }
    }

    public class Edge {
        @Override
        public String toString() {
            return String.format("%d -[%d]> %d", from, color, to);
        }

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
