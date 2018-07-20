import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Utils {

    public static final String[] colors = new String[]{"black", "red", "blue"};

    public static void printGraphDot(ConnectedComponent component, String outFile) {
        try (PrintWriter out = new PrintWriter(outFile)) {
            out.println("graph {");
            for (Edge e : component.originalEdges) {
                out.printf("%d -- %d [color = %s", e.from, e.to, colors[e.color]);
                if (e.color == 0) {
                    out.print(", weight = 100.0");
                } else {
                    out.print(", weight = 0");
                }
                out.println("];");
            }
            out.println("}");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        if (component.edges.size() > 100) {
            return;
        }
        try {
            String command = "circo -Tsvg -O" + outFile + ".svg " + outFile;
            Process runtime = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printFrequencyData(double[] freq, String outFile) {
        try (PrintWriter out = new PrintWriter(outFile)) {
            for (int i = 0; i < freq.length; i++) {
                out.println(i + " " + freq[i]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
