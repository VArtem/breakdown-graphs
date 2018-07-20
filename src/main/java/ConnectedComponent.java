import java.util.*;

public class ConnectedComponent {

    public static int BRUTE_FORCE_THRESHOLD = 100;
    public static boolean IGNORE_COLORS = false;

    private static final int[][] ALL_PERMUTATIONS = new int[][]{{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}};
    private static final int[] ID = ALL_PERMUTATIONS[0];
    private static final int FLOYD_ITERATIONS = 2;

    List<Edge>[] graph;
    List<Edge> edges, originalEdges;
    int[] vertexHashes;
    int hash = -1;

    public ConnectedComponent(List<Edge> edges) {
        this.originalEdges = this.edges = edges;
        if (edges.size() == 3) {
            return;
        }
        Map<Integer, Integer> vertexRemap = new TreeMap<>();
        for (Edge e : edges) {
            vertexRemap.put(e.from, 0);
            vertexRemap.put(e.to, 0);
        }
        int tmp = 0;
        for (int i : vertexRemap.keySet()) {
            vertexRemap.put(i, tmp++);
        }
        graph = new List[tmp];
        Arrays.setAll(graph, ArrayList::new);
        this.edges = new ArrayList<>();
        for (Edge e : edges) {
            Edge renamed = new Edge(vertexRemap.get(e.from), vertexRemap.get(e.to), e.color);
            this.edges.add(renamed);
            graph[renamed.from].add(renamed);
            graph[renamed.to].add(renamed);
        }
        if (edges.size() > 3) {
            calculateHashes();
        }
    }

    private void calculateHashes() {
        final long INF = Long.MAX_VALUE / 3;
        long[][] a = new long[graph.length][graph.length];
        vertexHashes = new int[graph.length];
        Arrays.fill(vertexHashes, 1);
        for (int IT = 0; IT < FLOYD_ITERATIONS; IT++) {
            for (int i = 0; i < a.length; i++) {
                Arrays.fill(a[i], INF);
                a[i][i] = 0;
            }
            for (Edge e : edges) {
                long add = ((long) vertexHashes[e.from] * vertexHashes[e.to]) & ((1 << 30) - 1) * (100 + (IGNORE_COLORS ? 0 : e.color));
                a[e.from][e.to] += add;
                a[e.to][e.from] += add;
            }
            for (int k = 0; k < a.length; k++) {
                for (int i = 0; i < a.length; i++) {
                    for (int j = 0; j < a.length; j++) {
                        a[i][j] = Math.min(a[i][j], a[i][k] + a[k][j]);
                    }
                }
            }
            for (int i = 0; i < a.length; i++) {
                Arrays.sort(a[i]);
                vertexHashes[i] = Arrays.hashCode(a[i]);
            }
        }
        this.hash = Arrays.hashCode(vertexHashes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectedComponent that = (ConnectedComponent) o;
        return areIsomorphic(this, that);
    }

    @Override
    public int hashCode() {
        if (edges.size() == 3) {
            return hash;
        }
        if (hash != -1) {
            return hash;
        }
        return hash;
    }

    public static boolean areIsomorphic(ConnectedComponent first, ConnectedComponent second) {
        if (first.edges.size() != second.edges.size()) {
            return false;
        }
        if (first.edges.size() == 3) {
            return true;
        }
        if (first.edges.size() > BRUTE_FORCE_THRESHOLD) {
            return false;
        }

        int n = first.graph.length;
        int[] matching = new int[n];
        int[][] firstMatrix = first.buildMatrix();
        for (int[] colorsPermutation : new int[][]{ID}) {
            int[][] secondMatrix = second.buildMatrix(colorsPermutation);
            if (!Arrays.equals(getCnt(firstMatrix), getCnt(secondMatrix))) {
                continue;
            }
            if (!match(first, second, colorsPermutation)) {
                continue;
            }
            Arrays.fill(matching, -1);
            if (go(0, n, matching, first, firstMatrix, secondMatrix)) {
                return true;
            }
            break;
        }
        return false;
    }

    private static boolean match(ConnectedComponent first, ConnectedComponent second, int[] perm) {
        for (int mask = 1; mask < 8; mask++) {
            int hashDist1 = bfs(first, ID, mask);
            int hashDist2 = bfs(second, perm, mask);
            if (hashDist1 != hashDist2) {
                return false;
            }
        }
        return true;
    }

    private static int bfs(ConnectedComponent comp, int[] perm, int mask) {
        int n = comp.graph.length;
        int[] dist = new int[n];
        int[] q = new int[n];
        List<Integer> hash = new ArrayList<>();
        for (int start = 0; start < n; start++) {
            Arrays.fill(dist, -1);
            dist[start] = 0;
            int head = 0, tail = 0;
            q[tail++] = start;
            while (head < tail) {
                int cur = q[head++];
                for (Edge e : comp.graph[cur]) {
                    int to = e.from + e.to - cur;
                    if (dist[to] == -1 && ((mask >> perm[e.color]) & 1) != 0) {
                        dist[to] = dist[cur] + 1;
                        q[tail++] = to;
                    }
                }
            }
            Arrays.sort(dist);
            hash.add(Arrays.hashCode(dist));
        }
        Collections.sort(hash);
        return hash.hashCode();
    }

    private static int[] getCnt(int[][] secondMatrix) {
        int[] cnt = new int[8];
        for (int[] i : secondMatrix) {
            for (int j : i) {
                cnt[j]++;
            }
        }
        return cnt;
    }

    private int[][] buildMatrix() {
        return buildMatrix(ID);
    }


    private int[][] buildMatrix(int[] perm) {
        int[][] a = new int[graph.length][graph.length];
        for (Edge e : edges) {
            a[e.from][e.to] |= 1 << perm[e.color];
            a[e.to][e.from] |= 1 << perm[e.color];
        }
        return a;
    }

    private static boolean go(int depth, int n, int[] matching, ConnectedComponent first, int[][] a, int[][] b) {
        if (depth == n) {
            return true;
        }
        int curVertex = findMaxVertex(matching, first);
        for (int i = 0; i < n; i++) {
            boolean ok = true;
            for (int j = 0; j < n; j++) {
                if (matching[j] == i) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                for (int j = 0; j < n; j++) {
                    if (matching[j] != -1) {
                        if (a[curVertex][j] != b[i][matching[j]]) {
                            ok = false;
                            break;
                        }
                    }
                }
                if (ok) {
                    matching[curVertex] = i;
                    if (go(depth + 1, n, matching, first, a, b)) {
                        return true;
                    }
                    matching[curVertex] = -1;
                }
            }
        }
        return false;
    }

    private static int findMaxVertex(int[] matching, ConnectedComponent first) {
        int max = 0, v = 0;
        for (int i = 0; i < matching.length; i++) {
            if (matching[i] == -1) {
                int cnt = 0;
                for (Edge e : first.graph[i]) {
                    if (matching[e.from + e.to - i] != -1) {
                        cnt++;
                    }
                }
                if (cnt > max) {
                    max = cnt;
                    v = i;
                }
            }
        }
        return v;
    }

}
