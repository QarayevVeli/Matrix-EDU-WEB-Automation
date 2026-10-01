package az.matrix.edu.pages;

import az.matrix.edu.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Logger log = LogManager.getLogger(getClass());

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeout")));
    }

    protected WebElement visible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected WebElement present(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    protected List<WebElement> all(By by) {
        return driver.findElements(by);
    }

    protected void click(By by) {
        log.info("Klik: {}", by);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(by)).click();
        } catch (ElementClickInterceptedException | StaleElementReferenceException e) {
            log.warn("Adi klik alınmadı, JS klik istifadə olunur: {}", by);
            jsClick(by);
        }
    }

    protected void jsClick(By by) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", present(by));
    }

    protected void type(By by, String text) {
        log.info("Yazılır: {} -> '{}'", by, text.length() > 40 ? text.substring(0, 40) + "..." : text);
        enter(by, text);
    }

    protected void typeSecret(By by, String text) {
        log.info("Gizli sahəyə yazılır: {}", by);
        enter(by, text);
    }

    private void enter(By by, String text) {
        WebElement element = visible(by);
        element.click();
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        if (!text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    protected void setValueJs(By by, String value) {
        log.info("JS ilə dəyər təyin olunur: {} -> {}", by, value);
        ((JavascriptExecutor) driver).executeScript(
                "const e=arguments[0];"
                        + "const setter=Object.getOwnPropertyDescriptor(HTMLInputElement.prototype,'value').set;"
                        + "setter.call(e,arguments[1]);"
                        + "e.dispatchEvent(new Event('input',{bubbles:true}));"
                        + "e.dispatchEvent(new Event('change',{bubbles:true}));"
                        + "e.dispatchEvent(new Event('blur',{bubbles:true}));",
                present(by), value);
    }

    protected void selectByText(By by, String text) {
        log.info("Seçilir: {} -> {}", by, text);
        new Select(visible(by)).selectByVisibleText(text);
    }

    protected List<String> optionTexts(By by) {
        return new Select(visible(by)).getOptions().stream()
                .map(option -> option.getText().trim())
                .collect(Collectors.toList());
    }

    protected <T> T stable(Supplier<T> action) {
        for (int attempt = 0; attempt < 4; attempt++) {
            try {
                return action.get();
            } catch (StaleElementReferenceException e) {
                log.debug("Element yenilənib, təkrar cəhd: {}", attempt + 1);
                pause(250);
            }
        }
        return action.get();
    }

    protected String textOf(By by) {
        return stable(() -> visible(by).getText().trim());
    }

    protected String contentOf(By by) {
        return stable(() -> {
            String content = present(by).getAttribute("textContent");
            return content == null ? "" : content.trim();
        });
    }

    protected String valueOf(By by) {
        return stable(() -> {
            String value = visible(by).getAttribute("value");
            return value == null ? "" : value;
        });
    }

    protected String attributeOf(By by, String name) {
        return stable(() -> {
            String value = present(by).getAttribute(name);
            return value == null ? "" : value;
        });
    }

    protected boolean isDisplayed(By by) {
        try {
            List<WebElement> elements = driver.findElements(by);
            return !elements.isEmpty() && elements.get(0).isDisplayed();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    protected boolean appearsWithin(By by, int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds)).until(ExpectedConditions.visibilityOfElementLocated(by));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void waitInvisible(By by) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    protected void chooseNgOption(String ngSelectCss, String optionText) {
        log.info("ng-select seçimi: {} -> {}", ngSelectCss, optionText);
        click(By.cssSelector(ngSelectCss + " .ng-select-container"));
        click(By.xpath("//div[contains(@class,'ng-dropdown-panel')]//div[contains(@class,'ng-option') and normalize-space(.)='"
                + optionText + "']"));
    }

    protected String chooseFirstNgOption(String ngSelectCss) {
        log.info("ng-select ilk seçim: {}", ngSelectCss);
        click(By.cssSelector(ngSelectCss + " .ng-select-container"));
        By option = By.cssSelector(".ng-dropdown-panel .ng-option");
        String text = visible(option).getAttribute("textContent").trim();
        click(option);
        return text;
    }

    protected List<String> ngOptionTexts(String ngSelectCss) {
        click(By.cssSelector(ngSelectCss + " .ng-select-container"));
        pause(600);
        List<String> texts = driver.findElements(By.cssSelector(".ng-dropdown-panel .ng-option")).stream()
                .map(option -> option.getAttribute("textContent").trim())
                .collect(Collectors.toList());
        click(By.cssSelector(".modal.show .modal-title"));
        return texts;
    }

    protected String ngSelectedText(String ngSelectCss) {
        List<WebElement> values = driver.findElements(By.cssSelector(ngSelectCss + " .ng-value-label"));
        return values.isEmpty() ? "" : values.get(0).getAttribute("textContent").trim();
    }

    protected void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}