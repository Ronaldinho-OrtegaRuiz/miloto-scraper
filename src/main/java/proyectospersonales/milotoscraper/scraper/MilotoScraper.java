package proyectospersonales.milotoscraper.scraper;


import org.openqa.selenium.WebDriver;

import org.springframework.stereotype.Service;
import proyectospersonales.milotoscraper.config.WebDriverManager;

@Service
public class MilotoScraper {

    private final WebDriver driver;

    public MilotoScraper(WebDriverManager webDriverManager) {
        this.driver = webDriverManager.getDriver();
    }

    public void scrape() {
    }


}
