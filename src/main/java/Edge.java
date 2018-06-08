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
