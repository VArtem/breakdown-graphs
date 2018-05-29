import org.jfree.data.statistics.HistogramDataset;

import java.util.*;

public class Histogram {

    private static final int COMPRESS_COEF = 1;//10;
    private HistogramDataset dataset;
    private XYBarRenderer renderer;
    private Map<Integer, Integer> freq;

    public Histogram(Map<Integer, Integer> freq) {
        this.freq = freq;
    }

    private ChartPanel createChartPanel() {
        dataset = new HistogramDataset();
        int shift = Collections.min(freq.keySet());
        double[] r = new double[1 + (Collections.max(freq.keySet()) - shift) / COMPRESS_COEF];
        for (int i : freq.keySet()) {
            r[(i - shift) / COMPRESS_COEF] += (double) freq.get(i) / COMPRESS_COEF;
        }
        dataset.addSeries("Frequency", r, 256);
        JFreeChart chart = ChartFactory.createHistogram("Частотность метаболитов в реакциях",
                "Количество реакций", "Количество метаболитов",
                dataset, PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(new Color(0xffffffff, true));
        XYPlot plot = (XYPlot) chart.getPlot();
        renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardXYBarPainter());
        Paint[] paintArray = {
                new Color(0xff000000, true),
        };
        plot.setDrawingSupplier(new DefaultDrawingSupplier(
                paintArray,
                DefaultDrawingSupplier.DEFAULT_FILL_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_PAINT_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_OUTLINE_STROKE_SEQUENCE,
                DefaultDrawingSupplier.DEFAULT_SHAPE_SEQUENCE));
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        return panel;
    }

    private class VisibleAction extends AbstractAction {

        VisibleAction() {
            putValue(NAME, dataset.getSeriesKey(0));
            putValue(SELECTED_KEY, true);
            renderer.setSeriesVisible(0, true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            renderer.setSeriesVisible(0, !renderer.getSeriesVisible(0));
        }
    }

    private void display() {
        JFrame f = new JFrame("Histogram");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.add(createChartPanel());
        JPanel panel = new JPanel();
        panel.add(new JCheckBox(new VisibleAction()));
        f.add(panel, BorderLayout.SOUTH);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    void launch() {
        EventQueue.invokeLater(this::display);
    }
}