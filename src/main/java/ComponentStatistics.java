import java.util.HashMap;
import java.util.Map;

public class ComponentStatistics {

    Map<ConnectedComponent, Integer> count;

    public ComponentStatistics() {
        count = new HashMap<>();
    }

    public void addComponent(ConnectedComponent component) {
        count.merge(component, 1, Integer::sum);
    }

    public int size() {
        return count.size();
    }
}
