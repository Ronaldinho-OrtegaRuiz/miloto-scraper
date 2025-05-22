package proyectospersonales.milotoscraper.scraper;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import proyectospersonales.milotoscraper.util.CSVUtil;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Service
public class MilotoScraper {

    private final WebDriver driver;
    private boolean detenerScraping = false;

    public MilotoScraper(WebDriver driver) {
        this.driver = driver;
    }

    public void scrape() {
        driver.get("https://www.baloto.com/miloto/resultados");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement resultTable = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("table-points-miloto")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", resultTable);
            Thread.sleep(2000);
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar la primera tabla", e);
        }

        scrapeAllPages();
    }

    private void scrapeAllPages() {
        String firstDateInCSV = CSVUtil.getMostRecentDate("data/results.csv");
        LocalDate lastDateParsed = null;

        try {
            if (firstDateInCSV != null) {
                lastDateParsed = parseFechaMiloto(firstDateInCSV.trim());
            }
        } catch (Exception e) {
            System.out.println("Error al interpretar la última fecha del archivo: " + e.getMessage());
        }

        while (!detenerScraping) {
            List<String[]> data = new ArrayList<>();

            WebElement table = driver.findElement(By.cssSelector("table.table.table-hover.table-points-miloto"));
            List<WebElement> rows = table.findElements(By.cssSelector("tbody > tr"));

            for (WebElement row : rows) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() >= 2) {
                    String date = cells.get(0).getText().trim();
                    LocalDate currentDate;

                    try {
                        currentDate = parseFechaMiloto(date);
                    } catch (Exception e) {
                        System.out.println("Error al interpretar fecha: '" + date + "' -> " + e.getMessage());
                        continue;
                    }

                    if (lastDateParsed != null) {
                        System.out.println("Comparando fechas:");
                        System.out.println("Fecha actual scrapeada: " + currentDate);
                        System.out.println("Última fecha en CSV    : " + lastDateParsed);

                        if (!currentDate.isAfter(lastDateParsed)) {
                            System.out.println("Detenido. La fecha actual (" + currentDate + ") ya está registrada.");
                            detenerScraping = true;
                            break;
                        } else {
                            System.out.println("Fecha " + currentDate + " es más reciente. Se agregará.");
                        }
                    } else {
                        System.out.println("No hay fecha previa en CSV. Se agregará todo.");
                    }


                    List<WebElement> spanNumbers = cells.get(1).findElements(By.tagName("span"));
                    if (spanNumbers.size() == 5) {
                        List<String> numbers = new ArrayList<>();
                        for (WebElement span : spanNumbers) {
                            numbers.add(span.getText().trim());
                        }

                        data.add(new String[]{
                                date,
                                numbers.get(0),
                                numbers.get(1),
                                numbers.get(2),
                                numbers.get(3),
                                numbers.get(4)
                        });
                    } else {
                        System.out.println("Fila con fecha " + date + " no tiene exactamente 5 números.");
                    }
                }
            }

            if (!data.isEmpty()) {
                CSVUtil.saveInCSV("data/results.csv", data);
            }

            if (detenerScraping) {
                System.out.println("Scraping finalizado. No se paginará más.");
                return;
            }

            List<WebElement> nextButtons = driver.findElements(By.cssSelector("a.btn.btn-pink.w-100"));
            Optional<WebElement> nextButton = nextButtons.stream()
                    .filter(b -> b.getText().trim().equalsIgnoreCase("Siguiente"))
                    .findFirst();

            if (nextButton.isPresent()) {
                try {
                    WebElement button = nextButton.get();
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", button);

                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    wait.until(ExpectedConditions.elementToBeClickable(button));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);

                    Thread.sleep(2000);

                } catch (Exception e) {
                    System.out.println("Error al hacer clic en el botón 'Siguiente': " + e.getMessage());
                    return;
                }
            } else {
                System.out.println("No hay botón 'Siguiente'. Fin de scraping.");
                return;
            }
        }
    }

    private LocalDate parseFechaMiloto(String texto) {
        Map<String, String> meses = Map.ofEntries(
                Map.entry("Enero", "01"),
                Map.entry("Febrero", "02"),
                Map.entry("Marzo", "03"),
                Map.entry("Abril", "04"),
                Map.entry("Mayo", "05"),
                Map.entry("Junio", "06"),
                Map.entry("Julio", "07"),
                Map.entry("Agosto", "08"),
                Map.entry("Septiembre", "09"),
                Map.entry("Octubre", "10"),
                Map.entry("Noviembre", "11"),
                Map.entry("Diciembre", "12")
        );

        try {
            String[] partes = texto.split(" ");
            if (partes.length != 5) throw new IllegalArgumentException("Formato inesperado: " + texto);

            String dia = partes[0].length() == 1 ? "0" + partes[0] : partes[0];
            String mesNombre = partes[2];
            String anio = partes[4];

            String mes = meses.get(mesNombre);
            if (mes == null) throw new IllegalArgumentException("Mes no reconocido: " + mesNombre);

            String fechaFormateada = String.format("%s-%s-%s", anio, mes, dia);
            return LocalDate.parse(fechaFormateada);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo parsear fecha personalizada: '" + texto + "'", e);
        }
    }
}
