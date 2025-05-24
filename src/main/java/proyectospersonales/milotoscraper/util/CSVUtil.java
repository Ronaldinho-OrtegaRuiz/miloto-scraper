package proyectospersonales.milotoscraper.util;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CSVUtil {

    public static void saveInCSV(String filePath, List<String[]> newData) {
        List<String[]> allData = new ArrayList<>();

        String[] headers = {"Fecha", "bola1", "bola2", "bola3", "bola4", "bola5"};

        File file = new File(filePath);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                reader.readLine();
                String line;
                while ((line = reader.readLine()) != null) {
                    allData.add(line.split(","));
                }
            } catch (IOException e) {
                System.out.println("Error al leer el archivo CSV: " + e.getMessage());
            }
        }

        allData.addAll(0, newData);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
        allData.sort((row1, row2) -> {
            try {
                LocalDate date1 = LocalDate.parse(row1[0].trim(), formatter);
                LocalDate date2 = LocalDate.parse(row2[0].trim(), formatter);
                return date2.compareTo(date1);
            } catch (Exception e) {
                return 0;
            }
        });

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(String.join(",", headers));
            writer.write("\n");
            for (String[] row : allData) {
                writer.write(String.join(",", row));
                writer.write("\n");
            }
        } catch (IOException e) {
            System.out.println("Error al guardar en CSV: " + e.getMessage());
        }
    }


    public static String getMostRecentDate(String filePath) {
        String firstDate = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String header = reader.readLine();
            String line = reader.readLine();
            if (line != null) {
                String[] columns = line.split(",");
                if (columns.length > 0) {
                    firstDate = columns[0].trim();
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo CSV: " + e.getMessage());
        }
        return firstDate;
    }

    public static List<List<String>> getDataFromCSV(String filePath) {
        List<List<String>> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                List<String> row = new ArrayList<>();
                for (String column : columns) {
                    row.add(column.trim());
                }
                data.add(row);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo CSV: " + e.getMessage());
        }

        return data;
    }

    public static void saveStreaksToCSV(Map<String, String> streaks, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Escribir encabezados
            writer.append("Número,Racha\n");

            // Escribir datos
            for (Map.Entry<String, String> entry : streaks.entrySet()) {
                writer.append(entry.getKey())
                        .append(",")
                        .append(entry.getValue())
                        .append("\n");
            }

            System.out.println("Archivo CSV generado en: " + filePath);
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo CSV: " + e.getMessage());
        }
    }

    public static void saveMostCommonPairsToCSV(int[][] cooccurrenceMatrix, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.append("Par,Frecuencia\n");

            for (int i = 0; i < cooccurrenceMatrix.length; i++) {
                for (int j = i + 1; j < cooccurrenceMatrix[i].length; j++) {
                    if (cooccurrenceMatrix[i][j] > 0) {
                        String pair = String.format("(%02d, %02d)", i + 1, j + 1);
                        writer.append(pair)
                                .append(",")
                                .append(String.valueOf(cooccurrenceMatrix[i][j]))
                                .append("\n");
                    }
                }
            }

            System.out.println("Archivo CSV generado en: " + filePath);
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo CSV: " + e.getMessage());
        }
    }
}