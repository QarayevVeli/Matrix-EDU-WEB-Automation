package az.matrix.edu.driver;

import az.matrix.edu.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public final class DriverFactory {

    private static final Logger LOG = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = ConfigReader.get("browser").trim().toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless");
        LOG.info("Brauzer işə salınır: {}, headless={}", browser, headless);
        WebDriver driver;
        switch (browser) {
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                if (headless) {
                    options.addArguments("-headless");
                }
                driver = new FirefoxDriver(options);
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                options.addArguments("--window-size=1920,1080", "--disable-notifications");
                if (headless) {
                    options.addArguments("--headless=new");
                }
                driver = new EdgeDriver(options);
            }
            default -> {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--window-size=1920,1080", "--disable-notifications", "--remote-allow-origins=*");
                if (headless) {
                    options.addArguments("--headless=new");
                }
                driver = new ChromeDriver(options);
            }
        }
        if (headless) {
            driver.manage().window().setSize(new Dimension(1920, 1080));
        } else {
            driver.manage().window().maximize();
        }
        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            LOG.info("Brauzer bağlanır");
            driver.quit();
            DRIVER.remove();
        }
    }
}
