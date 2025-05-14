package proyectospersonales.milotoscraper;

import org.openqa.selenium.WebDriver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import proyectospersonales.milotoscraper.config.SeleniumConfig;
import proyectospersonales.milotoscraper.scraper.MilotoScraper;


@SpringBootApplication
public class MilotoScraperApplication implements CommandLineRunner {

    private final MilotoScraper milotoScraper;

    public MilotoScraperApplication(MilotoScraper milotoScraper) {
        this.milotoScraper = milotoScraper;
    }


    public static void main(String[] args) {
        SpringApplication.run(MilotoScraperApplication.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SeleniumConfig.class);
        WebDriver driver = context.getBean(WebDriver.class);

        MilotoScraper scraper = new MilotoScraper(driver);
        scraper.scrape();

        //driver.quit();
    }

}
