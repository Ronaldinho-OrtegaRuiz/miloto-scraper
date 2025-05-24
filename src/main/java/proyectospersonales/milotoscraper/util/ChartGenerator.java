package proyectospersonales.milotoscraper.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.stream.IntStream;

public class ChartGenerator {

    public static void generateFrequencyChart(Map<String, Integer> frequencies, String outputPath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        frequencies.forEach((number, frequency) ->
                dataset.addValue(frequency, "Frecuencia", number));

        JFreeChart barChart = ChartFactory.createBarChart(
                "Frecuencia de Números",
                "Número",
                "Frecuencia",
                dataset
        );

        //Estilo oscuro
        barChart.setBackgroundPaint(Color.DARK_GRAY);
        barChart.getTitle().setPaint(Color.WHITE);
        barChart.getLegend().setBackgroundPaint(Color.DARK_GRAY);
        barChart.getLegend().setItemPaint(Color.WHITE);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);

        //Estilo de las barras
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(30, 144, 255, 200)); 
        renderer.setDrawBarOutline(false);

        //Eje X
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        xAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 10));
        xAxis.setTickLabelPaint(Color.WHITE);
        xAxis.setLabelPaint(Color.WHITE);

        //Eje Y
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);

        try {
            ChartUtils.saveChartAsPNG(new File(outputPath), barChart, 1000, 600);
        } catch (IOException e) {
            System.out.println("Error al guardar el gráfico: " + e.getMessage());
        }
    }

    public static void generateAverageIntervalChart(Map<String, Double> intervals, String outputPath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        intervals.forEach((number, interval) ->
                dataset.addValue(interval, "Promedio de Sorteos", number));

        JFreeChart barChart = ChartFactory.createBarChart(
                "Promedio de Sorteos por Número",
                "Número",
                "Promedio de Sorteos",
                dataset
        );

        // Estilo oscuro
        barChart.setBackgroundPaint(Color.DARK_GRAY);
        barChart.getTitle().setPaint(Color.WHITE);
        barChart.getLegend().setBackgroundPaint(Color.DARK_GRAY);
        barChart.getLegend().setItemPaint(Color.WHITE);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);

        // Estilo de las barras
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(34, 139, 34, 200)); // Verde oscuro
        renderer.setDrawBarOutline(false);

        // Eje X
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        xAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 10));
        xAxis.setTickLabelPaint(Color.WHITE);
        xAxis.setLabelPaint(Color.WHITE);

        // Eje Y
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);

        try {
            ChartUtils.saveChartAsPNG(new File(outputPath), barChart, 1000, 600);
        } catch (IOException e) {
            System.out.println("Error al guardar el gráfico: " + e.getMessage());
        }
    }

    public static void generateComparativeFrequencyChart(Map<String, Integer> totalFrequencies, Map<String, Integer> yearFrequencies, String outputPath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        totalFrequencies.forEach((number, frequency) ->
                dataset.addValue(frequency, "Frecuencia Total", number));

        yearFrequencies.forEach((number, frequency) ->
                dataset.addValue(frequency, "Frecuencia 2025", number));

        JFreeChart barChart = ChartFactory.createBarChart(
                "Frecuencia Comparativa de Números",
                "Número",
                "Frecuencia",
                dataset
        );

        // Estilo oscuro
        barChart.setBackgroundPaint(Color.DARK_GRAY);
        barChart.getTitle().setPaint(Color.WHITE);
        barChart.getLegend().setBackgroundPaint(Color.DARK_GRAY);
        barChart.getLegend().setItemPaint(Color.WHITE);

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);

        // Estilo de las barras
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(30, 144, 255, 200)); // Azul
        renderer.setSeriesPaint(1, new Color(34, 139, 34, 200)); // Verde
        renderer.setDrawBarOutline(false);

        // Eje X
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        xAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 10));
        xAxis.setTickLabelPaint(Color.WHITE);
        xAxis.setLabelPaint(Color.WHITE);

        // Eje Y
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);

        try {
            ChartUtils.saveChartAsPNG(new File(outputPath), barChart, 1000, 600);
        } catch (IOException e) {
            System.out.println("Error al guardar el gráfico: " + e.getMessage());
        }
    }

    public static void generateHistoricalTrendChart(Map<String, List<Integer>> numberTrends, List<String> dates, String outputPath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Map.Entry<String, List<Integer>> entry : numberTrends.entrySet()) {
            String number = entry.getKey();
            List<Integer> trend = entry.getValue();

            for (int i = 0; i < dates.size(); i++) {
                dataset.addValue(trend.get(i), "Número " + number, dates.get(i));
            }
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Tendencia Histórica de Números",
                "Fecha",
                "Aparición (1 = Sí, 0 = No)",
                dataset
        );

        // Estilo oscuro
        lineChart.setBackgroundPaint(Color.DARK_GRAY);
        lineChart.getTitle().setPaint(Color.WHITE);
        lineChart.getLegend().setBackgroundPaint(Color.DARK_GRAY);
        lineChart.getLegend().setItemPaint(Color.WHITE);

        CategoryPlot plot = lineChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);

        // Eje X
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        xAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 10));
        xAxis.setTickLabelPaint(Color.WHITE);
        xAxis.setLabelPaint(Color.WHITE);

        // Eje Y
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);

        try {
            ChartUtils.saveChartAsPNG(new File(outputPath), lineChart, 1200, 800);
        } catch (IOException e) {
            System.out.println("Error al guardar el gráfico: " + e.getMessage());
        }
    }

    public static void generateHeatmap(int[][] heatmapData, String outputPath) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        String[] mesesNombre = {
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        };

        int[] totalPorNumero = new int[39];
        for (int i = 0; i < 39; i++) {
            for (int j = 0; j < 12; j++) {
                totalPorNumero[i] += heatmapData[i][j];
            }
        }

        Integer[] indicesOrdenados = IntStream.range(0, 39)
                .boxed()
                .sorted((a, b) -> Integer.compare(totalPorNumero[b], totalPorNumero[a]))
                .limit(10)
                .toArray(Integer[]::new);

        for (int idx : indicesOrdenados) {
            for (int mes = 0; mes < 12; mes++) {
                dataset.addValue(heatmapData[idx][mes], "Número " + (idx + 1), mesesNombre[mes]);
            }
        }

        JFreeChart heatmapChart = ChartFactory.createBarChart(
                "Mapa de Calor de Frecuencia de Números",
                "Mes",
                "Frecuencia",
                dataset
        );

        // Estilo oscuro
        heatmapChart.setBackgroundPaint(Color.DARK_GRAY);
        heatmapChart.getTitle().setPaint(Color.WHITE);
        heatmapChart.getLegend().setBackgroundPaint(Color.DARK_GRAY);
        heatmapChart.getLegend().setItemPaint(Color.WHITE);

        CategoryPlot plot = heatmapChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.WHITE);

        // Eje X
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        xAxis.setTickLabelFont(new Font("SansSerif", Font.BOLD, 12));
        xAxis.setTickLabelPaint(Color.WHITE);
        xAxis.setLabelPaint(Color.WHITE);

        // Eje Y
        plot.getRangeAxis().setTickLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelPaint(Color.WHITE);
        plot.getRangeAxis().setLabelFont(new Font("SansSerif", Font.BOLD, 12));

        try {
            ChartUtils.saveChartAsPNG(new File(outputPath), heatmapChart, 1400, 900);
        } catch (IOException e) {
            System.out.println("Error al guardar el gráfico: " + e.getMessage());
        }
    }

}
