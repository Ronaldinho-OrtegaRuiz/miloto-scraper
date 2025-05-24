package proyectospersonales.milotoscraper.analyzer;

import org.springframework.stereotype.Service;
import proyectospersonales.milotoscraper.util.CSVUtil;
import proyectospersonales.milotoscraper.util.ChartGenerator;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class MilotoAnalyzer {

    public void analyze() {
        System.out.println("Iniciando análisis de datos...");
        analyzeData();
        System.out.println("Análisis de datos completado.");
    }

    private void analyzeData() {
        List<List<String>> data = CSVUtil.getDataFromCSV("data/results.csv");

        Map<String, Integer> frequenciesAll = calculateFrequenciesAll(data);
        Map<String, Double> averageIntervalsAll = calculateAverageIntervalAll(data);
        int year = 2025;
        Map<String, Integer> frequenciesByYear = calculateFrequenciesByYear(data, year);
        Map<String, Double> averageIntervalsByYear = calculateAverageIntervalByYear(data, year);
        List<String> dates = new ArrayList<>();
        Map<String, List<Integer>> numberTrends = calculateTop5NumberTrendsByYear(data,year,dates);
        int[][] heatmapData = calculateHeatmapData(data);
        int[][] heatmapData2025 = calculateHeatmapDataFor2025(data);
        Map<String,Double> stdDevs = calculateIntervalStdDev(data);

        Map<String, String> streaks = calculateStreaks(data);
        CSVUtil.saveStreaksToCSV(streaks, "data/streaks.csv");

        int[][] cooccurrenceMatrix = calculateCooccurrenceMatrix(data);
        CSVUtil.saveMostCommonPairsToCSV(cooccurrenceMatrix, "data/common_pairs.csv");



        System.out.println("Frecuencia de cada número en general:");
        frequenciesAll.forEach((number, frequency) ->
                System.out.println("Número: " + number + ", Frecuencia: " + frequency)
        );

        System.out.println("Promedio de sorteos por número en general:");
        averageIntervalsAll.forEach((number, interval) ->
                System.out.println("Número: " + number + ", Promedio: " + interval)
        );

        System.out.println("Frecuencia de cada número en " + year + ":");
        frequenciesByYear.forEach((number, frequency) ->
                System.out.println("Número: " + number + ", Frecuencia: " + frequency)
        );

        System.out.println("Promedio de sorteos por número en " + year + ":");
        averageIntervalsByYear.forEach((number, interval) ->
                System.out.println("Número: " + number + ", Promedio: " + interval)
        );

        System.out.println("Tendencias de números:");
        numberTrends.forEach((number, trend) -> {
            System.out.print("Número: " + number + ", Tendencia: ");
            for (int i = 0; i < trend.size(); i++) {
                System.out.print(trend.get(i) + (i < trend.size() - 1 ? ", " : ""));
            }
            System.out.println();
        });

        System.out.println("Datos de calor:");
        stdDevs.forEach((number, stdDev) ->
                System.out.println("Número: " + number + ", Desviación estándar: " + stdDev)
        );

        calculateSuggestedNumbers(data, year, frequenciesAll, frequenciesByYear, averageIntervalsAll, stdDevs, streaks);

        ChartGenerator.generateFrequencyChart(frequenciesAll, "chart_data/frequency_chart_all.png");
        ChartGenerator.generateAverageIntervalChart(averageIntervalsAll, "chart_data/average_interval_chart_all.png");
        ChartGenerator.generateFrequencyChart(frequenciesByYear, "chart_data/frequency_chart_" + year + ".png");
        ChartGenerator.generateAverageIntervalChart(averageIntervalsByYear, "chart_data/average_interval_chart_" + year + ".png");
        ChartGenerator.generateComparativeFrequencyChart(frequenciesAll, frequenciesByYear, "chart_data/comparative_frequency_chart.png");
        ChartGenerator.generateHistoricalTrendChart(numberTrends, dates, "chart_data/historical_trend_chart.png");
        ChartGenerator.generateHeatmap(heatmapData,"chart_data/heatmap_chart.png");
        ChartGenerator.generateHeatmap(heatmapData2025,"chart_data/heatmap_2025.png");
    }

    private Map<String, Integer> calculateFrequenciesAll(List<List<String>> data) {
        Map<String, Integer> frequencies = new TreeMap<>();

        for (List<String> row : data) {
            if (row.size() >= 6) {
                for (int i = 1; i < row.size(); i++) {
                    String number = row.get(i).trim();
                    frequencies.put(number, frequencies.getOrDefault(number, 0) + 1);
                }
            }
        }

        return frequencies;
    }

    private Map<String, Integer> calculateFrequenciesByYear(List<List<String>> data, int year) {
        Map<String, Integer> frequencies = new TreeMap<>();

        for (List<String> row : data) {
            if (row.size() >= 6 && row.get(0).contains(String.valueOf(year))) {
                for (int i = 1; i < row.size(); i++) {
                    String number = row.get(i).trim();
                    frequencies.put(number, frequencies.getOrDefault(number, 0) + 1);
                }
            }
        }

        return frequencies;
    }

    private Map<String, Double> calculateAverageIntervalAll(List<List<String>> data) {
        Map<String, Integer> frequencies = new TreeMap<>();
        int totalDraws = 0;

        for (List<String> row : data) {
            if (row.size() >= 6) {
                totalDraws++;
                for (int i = 1; i < row.size(); i++) {
                    String number = row.get(i).trim();
                    frequencies.put(number, frequencies.getOrDefault(number, 0) + 1);
                }
            }
        }

        Map<String, Double> intervals = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
            intervals.put(entry.getKey(), (double) totalDraws / entry.getValue());
        }

        return intervals;
    }

    private Map<String, Double> calculateAverageIntervalByYear(List<List<String>> data, int year) {
        Map<String, Integer> frequencies = new TreeMap<>();
        int totalDraws = 0;

        for (List<String> row : data) {
            if (row.size() >= 6 && row.get(0).contains(String.valueOf(year))) {
                totalDraws++;
                for (int i = 1; i < row.size(); i++) {
                    String number = row.get(i).trim();
                    frequencies.put(number, frequencies.getOrDefault(number, 0) + 1);
                }
            }
        }

        Map<String, Double> intervals = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : frequencies.entrySet()) {
            intervals.put(entry.getKey(), (double) totalDraws / entry.getValue());
        }

        return intervals;
    }

    private Map<String, List<Integer>> calculateTop5NumberTrendsByYear(List<List<String>> data, int year, List<String> dates) {
        Map<String, List<Integer>> numberTrends = new TreeMap<>();

        for (List<String> row : data) {
            if (row.size() >= 6 && row.get(0).contains(String.valueOf(year))) {
                String date = row.get(0).trim();
                dates.add(date);

                Set<String> numbersToday = new HashSet<>();
                for (int i = 1; i < row.size(); i++) {
                    String number = row.get(i).trim();
                    numbersToday.add(number);
                    numberTrends.putIfAbsent(number, new ArrayList<>());
                }

                for (String number : numberTrends.keySet()) {
                    List<Integer> trend = numberTrends.get(number);
                    trend.add(numbersToday.contains(number) ? 1 : 0);
                }
            }
        }

        Map<String, List<Integer>> top5 = numberTrends.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(
                        e2.getValue().stream().mapToInt(Integer::intValue).sum(),
                        e1.getValue().stream().mapToInt(Integer::intValue).sum()
                ))
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        int totalFechas = dates.size();
        for (List<Integer> trend : top5.values()) {
            while (trend.size() < totalFechas) {
                trend.add(0);
            }
        }

        return top5;
    }

    private int[][] calculateHeatmapData(List<List<String>> data) {
        int[][] heatmap = new int[39][12];

        Map<String, Integer> meses = Map.ofEntries(
                Map.entry("Enero", 0), Map.entry("Febrero", 1), Map.entry("Marzo", 2),
                Map.entry("Abril", 3), Map.entry("Mayo", 4), Map.entry("Junio", 5),
                Map.entry("Julio", 6), Map.entry("Agosto", 7), Map.entry("Septiembre", 8),
                Map.entry("Octubre", 9), Map.entry("Noviembre", 10), Map.entry("Diciembre", 11)
        );

        for (List<String> row : data) {
            if (row.size() >= 6) {
                String fecha = row.get(0).trim();
                try {
                    String[] partes = fecha.split(" ");
                    if (partes.length < 5) {
                        System.out.println("Fecha con formato inesperado: " + fecha);
                        continue;
                    }

                    String mesNombre = partes[2];
                    Integer mes = meses.get(mesNombre);
                    if (mes == null) {
                        System.out.println("Mes no reconocido: " + mesNombre);
                        continue;
                    }

                    for (int i = 1; i < row.size(); i++) {
                        int numero = Integer.parseInt(row.get(i).trim()) - 1;
                        heatmap[numero][mes]++;
                    }
                } catch (Exception e) {
                    System.out.println("Error procesando fila: " + fecha + " → " + e.getMessage());
                }
            }
        }

        return heatmap;
    }
    private int[][] calculateHeatmapDataFor2025(List<List<String>> data) {
        int[][] heatmap = new int[39][12];

        Map<String, Integer> meses = Map.ofEntries(
                Map.entry("Enero", 0), Map.entry("Febrero", 1), Map.entry("Marzo", 2),
                Map.entry("Abril", 3), Map.entry("Mayo", 4), Map.entry("Junio", 5),
                Map.entry("Julio", 6), Map.entry("Agosto", 7), Map.entry("Septiembre", 8),
                Map.entry("Octubre", 9), Map.entry("Noviembre", 10), Map.entry("Diciembre", 11)
        );

        for (List<String> row : data) {
            if (row.size() >= 6) {
                String fecha = row.get(0).trim();
                try {
                    String[] partes = fecha.split(" ");
                    if (partes.length < 5 || !partes[4].equals("2025")) {
                        continue;
                    }

                    String mesNombre = partes[2];
                    Integer mes = meses.get(mesNombre);
                    if (mes == null) {
                        System.out.println("Mes no reconocido: " + mesNombre);
                        continue;
                    }

                    for (int i = 1; i < row.size(); i++) {
                        int numero = Integer.parseInt(row.get(i).trim()) - 1;
                        heatmap[numero][mes]++;
                    }
                } catch (Exception e) {
                    System.out.println("Error procesando fila: " + fecha + " → " + e.getMessage());
                }
            }
        }

        return heatmap;
    }

    private Map<String, String> calculateStreaks(List<List<String>> data) {
        Map<String, Integer> currentStreak = new TreeMap<>();
        Map<String, Integer> absenceStreak = new TreeMap<>();
        Map<String, String> streaks = new TreeMap<>();

        for (int i = 1; i <= 39; i++) {
            String number = String.format("%02d", i);
            currentStreak.put(number, 0);
            absenceStreak.put(number, 0);
        }

        for (int i = data.size() - 1; i >= 0; i--) {
            List<String> row = data.get(i);
            if (row.size() >= 6) {
                Set<String> numbersToday = row.subList(1, row.size()).stream()
                        .map(num -> String.format("%02d", Integer.parseInt(num.trim())))
                        .collect(Collectors.toSet());

                for (String number : currentStreak.keySet()) {
                    if (numbersToday.contains(number)) {
                        currentStreak.put(number, currentStreak.get(number) + 1);
                        absenceStreak.put(number, 0);
                    } else {
                        absenceStreak.put(number, absenceStreak.get(number) + 1);
                        currentStreak.put(number, 0);
                    }
                }
            }
        }

        for (String number : currentStreak.keySet()) {
            streaks.put(number, "Racha: " + currentStreak.get(number) + ", Ausencia: " + absenceStreak.get(number));
        }

        return streaks;
    }

    private int[][] calculateCooccurrenceMatrix(List<List<String>> data) {
        int[][] cooccurrenceMatrix = new int[39][39];

        for (List<String> row : data) {
            if (row.size() >= 6) {
                List<Integer> numbers = row.subList(1, row.size()).stream()
                        .map(num -> Integer.parseInt(num.trim()))
                        .collect(Collectors.toList());

                for (int i = 0; i < numbers.size(); i++) {
                    for (int j = i + 1; j < numbers.size(); j++) {
                        int num1 = numbers.get(i) - 1; // Índices base 0
                        int num2 = numbers.get(j) - 1;
                        cooccurrenceMatrix[num1][num2]++;
                        cooccurrenceMatrix[num2][num1]++;
                    }
                }
            }
        }

        return cooccurrenceMatrix;
    }

    private Map<String, Double> calculateIntervalStdDev(List<List<String>> data) {
        Map<String, List<Integer>> apariciones = new TreeMap<>();
        for (int i = 0; i < data.size(); i++) {
            List<String> row = data.get(i);
            if (row.size() >= 6) {
                for (int j = 1; j < row.size(); j++) {
                    String num = row.get(j).trim();
                    apariciones.computeIfAbsent(num, k -> new ArrayList<>()).add(i);
                }
            }
        }

        Map<String, Double> stdDevs = new TreeMap<>();
        for (Map.Entry<String, List<Integer>> entry : apariciones.entrySet()) {
            List<Integer> positions = entry.getValue();
            List<Integer> gaps = new ArrayList<>();
            for (int i = 1; i < positions.size(); i++) {
                gaps.add(positions.get(i) - positions.get(i - 1));
            }
            double avg = gaps.stream().mapToInt(i -> i).average().orElse(0);
            double variance = gaps.stream().mapToDouble(i -> Math.pow(i - avg, 2)).average().orElse(0);
            stdDevs.put(entry.getKey(), Math.sqrt(variance));
        }

        return stdDevs;
    }

    private void calculateSuggestedNumbers(List<List<String>> data, int year,
                                           Map<String, Integer> freqAll,
                                           Map<String, Integer> freq2025,
                                           Map<String, Double> avgInterval,
                                           Map<String, Double> stdDevs,
                                           Map<String, String> streaks) {

        Map<String, Integer> absenceMap = new TreeMap<>();
        for (Map.Entry<String, String> entry : streaks.entrySet()) {
            String[] parts = entry.getValue().split(", Ausencia: ");
            absenceMap.put(entry.getKey(), Integer.parseInt(parts[1]));
        }

        Map<String, Double> scoreMap = new TreeMap<>();

        double maxFreq = Collections.max(freqAll.values());
        double maxFreq2025 = Collections.max(freq2025.values());
        double maxAbsence = Collections.max(absenceMap.values());
        double maxAvg = Collections.max(avgInterval.values());
        double maxStd = Collections.max(stdDevs.values());

        for (String num : freqAll.keySet()) {
            double score = 0.0;
            double freqScore = freqAll.getOrDefault(num, 0) / maxFreq;
            double freq2025Score = freq2025.getOrDefault(num, 0) / maxFreq2025;
            double avgScore = 1.0 - (avgInterval.getOrDefault(num, maxAvg) / maxAvg);
            double stdScore = 1.0 - (stdDevs.getOrDefault(num, maxStd) / maxStd);
            double absenceScore = absenceMap.getOrDefault(num, 0) / maxAbsence;

            score = freqScore * 0.25 + freq2025Score * 0.25 + avgScore * 0.2 + stdScore * 0.1 + absenceScore * 0.2;
            scoreMap.put(num, score);
        }

        System.out.println("\nRanking sugerido de números (basado en combinación de métricas):");
        scoreMap.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .forEach(entry -> System.out.printf("Número: %s | Score: %.4f%n", entry.getKey(), entry.getValue()));
    }

}
