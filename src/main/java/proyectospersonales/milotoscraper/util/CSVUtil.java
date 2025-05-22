package proyectospersonales.milotoscraper.util;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

}