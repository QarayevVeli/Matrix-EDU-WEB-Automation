package az.matrix.edu.hooks;

import az.matrix.edu.context.TestContext;
import az.matrix.edu.driver.DriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Hooks {

    private static final Logger LOG = LogManager.getLogger(Hooks.class);
    private final TestContext context;

    public Hooks(TestContext context) {
        this.context = context;
    }

    @Before
    public void setUp(Scenario scenario) {
        LOG.info("===== Ssenari başlayır: {} =====", scenario.getName());
        DriverFactory.createDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = DriverFactory.getDriver();
        try {
            if (driver != null && scenario.isFailed()) {
                LOG.error("Ssenari uğursuz oldu: {}", scenario.getName());
                try {
                    byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                    scenario.attach(screenshot, "image/png", scenario.getName());
                } catch (Exception e) {
                    LOG.warn("Screenshot alınmadı: {}", e.getMessage());
                }
            }
            if (!context.createdTrainings().isEmpty()) {
                LOG.warn("Təlimlərin silinməsi üçün funksiya yoxdur, sistemdə qalan test təlimləri: {}", context.createdTrainings());
            }
        } finally {
            LOG.info("===== Ssenari bitdi: {} | status: {} =====", scenario.getName(), scenario.getStatus());
            DriverFactory.quitDriver();
        }
    }
}