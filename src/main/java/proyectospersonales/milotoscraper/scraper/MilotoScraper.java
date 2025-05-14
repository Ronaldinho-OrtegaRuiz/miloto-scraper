package proyectospersonales.milotoscraper.scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class MilotoScraper {

    private final WebDriver driver;

    public MilotoScraper(WebDriver driver) {
        this.driver = driver;
    }

    public void scrape() {
        driver.get("https://www.baloto.com/miloto/resultados");
        moveTableToMiddle();
    }

    private void moveTableToMiddle() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            WebElement resultTable = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("table-points-miloto")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", resultTable);
            Thread.sleep(2000);
            getDataFromTable();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void getDataFromTable() {
        WebElement tabla = driver.findElement(By.cssSelector("table.table.table-hover.table-points-miloto"));
        List<WebElement> filas = tabla.findElements(By.cssSelector("tbody > tr"));

        for (WebElement fila : filas) {
            List<WebElement> celdas = fila.findElements(By.tagName("td"));
            if (celdas.size() >= 2) {
                String fecha = celdas.get(0).getText().trim();
                List<WebElement> numerosSpan = celdas.get(1).findElements(By.tagName("span"));
                List<String> numeros = new ArrayList<>();
                for (WebElement span : numerosSpan) {
                    numeros.add(span.getText().trim());
                }
                System.out.println("Fecha: " + fecha);
                System.out.println("Números: " + String.join(", ", numeros));
                System.out.println("-----------------------------------");
            }
        }

        List<WebElement> siguienteBotones = driver.findElements(By.cssSelector("a.btn.btn-pink.w-100"));
        for (WebElement boton : siguienteBotones) {
            if (boton.getText().trim().equalsIgnoreCase("Siguiente")) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", boton);

                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    wait.until(ExpectedConditions.elementToBeClickable(boton));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", boton);

                    Thread.sleep(2000);
                    getDataFromTable();
                } catch (Exception e) {
                    System.out.println("Error al hacer clic en el botón 'Siguiente': " + e.getMessage());
                }
                return;
            }
        }

        System.out.println("No hay más páginas.");
    }
}