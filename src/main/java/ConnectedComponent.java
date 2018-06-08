import java.util.List;

public class ConnectedComponent {
    List<Edge> edges;

    public ConnectedComponent(List<Edge> edges) {
        this.edges = edges;
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


        return false;
    }
}
