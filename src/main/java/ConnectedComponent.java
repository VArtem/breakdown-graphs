import java.util.*;

public class ConnectedComponent {

    public static int BRUTE_FORCE_THRESHOLD = 100;
    List<Edge>[] graph;
    List<Edge> edges, originalEdges;

    public ConnectedComponent(List<Edge> edges) {
        Map<Integer, Integer> vertexNumber = new TreeMap<>();
        for (Edge e : edges) {
            vertexNumber.put(e.from, 0);
            vertexNumber.put(e.to, 0);
        }
        int tmp = 0;
        for (int i : vertexNumber.keySet()) {
            vertexNumber.put(i, tmp++);
        }
        this.originalEdges = edges;
        graph = new List[tmp];
        Arrays.setAll(graph, ArrayList::new);
        this.edges = new ArrayList<>();
        for (Edge e : edges) {
            Edge renamed = new Edge(vertexNumber.get(e.from), vertexNumber.get(e.to), e.color);
            this.edges.add(renamed);
            graph[renamed.from].add(renamed);
            graph[renamed.to].add(renamed);
        }

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
        return edges.size();
    }

    public static boolean areIsomorphic(ConnectedComponent first, ConnectedComponent second) {
        if (first.edges.size() != second.edges.size()) {
            return false;
        }
        if (first.edges.size() > BRUTE_FORCE_THRESHOLD) {
            return false;
        }
        if (first.edges.size() == 3) {
            return true;
        }

        int n = first.graph.length;
        int[] matching = new int[n];
        int[][] firstMatrix = first.buildMatrix();
        for (int[] colorsPermutation: new int[][] {{0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}, {2, 1, 0}}) {
//        for (int[] colorsPermutation : new int[][]{{0, 1, 2}}) {
            int[][] secondMatrix = second.buildMatrix(colorsPermutation);
            Arrays.fill(matching, -1);
            if (go(0, n, matching, first, firstMatrix, secondMatrix)) {
                return true;
            }
        }
        return false;
    }

    private int[][] buildMatrix() {
        int[][] a = new int[graph.length][graph.length];
        for (Edge e : edges) {
            a[e.from][e.to] |= 1 << e.color;
            a[e.to][e.from] |= 1 << e.color;
        }
        return a;
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
