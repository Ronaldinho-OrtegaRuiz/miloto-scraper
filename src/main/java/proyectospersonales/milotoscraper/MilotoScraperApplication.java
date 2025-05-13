package proyectospersonales.milotoscraper;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
        milotoScraper.scrape();
    }

}
