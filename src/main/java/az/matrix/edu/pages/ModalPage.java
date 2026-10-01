package az.matrix.edu.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ModalPage extends BasePage {

    protected static final String MODAL_XPATH = "//div[contains(@class,'modal') and contains(@class,'show')]";
    protected static final String MODAL = ".modal.show";
    private static final By MODAL_BY = By.cssSelector(MODAL);
    private static final By TITLE = By.cssSelector(MODAL + " .modal-title");
    private static final By LABELS = By.cssSelector(MODAL + " label");
    private static final By ERRORS = By.cssSelector(MODAL + " small.text-danger");
    private static final By CLOSE = By.cssSelector(MODAL + " button.close");

    protected ModalPage(WebDriver driver) {
        super(driver);
    }

    public void waitOpen() {
        visible(TITLE);
        pause(500);
    }

    public boolean isOpen() {
        return isDisplayed(MODAL_BY);
    }

    public void waitClosed() {
        waitInvisible(MODAL_BY);
    }

    public boolean closesWithin(int seconds) {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(seconds))
                    .until(d -> d.findElements(MODAL_BY).stream().noneMatch(WebElement::isDisplayed));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String title() {
        return textOf(TITLE);
    }

    public List<String> labels() {
        return all(LABELS).stream()
                .map(label -> label.getAttribute("textContent").replace("*", "").trim())
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }

    public List<String> validationMessages() {
        pause(500);
        return all(ERRORS).stream()
                .filter(WebElement::isDisplayed)
                .map(element -> element.getAttribute("textContent").trim())
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
    }

    public void clickButton(String text) {
        click(By.xpath(MODAL_XPATH + "//button[contains(normalize-space(.),'" + text + "')]"));
    }

    public boolean hasButton(String text) {
        return isDisplayed(By.xpath(MODAL_XPATH + "//button[contains(normalize-space(.),'" + text + "')]"));
    }

    public void cancel() {
        clickButton("Ləğv et");
    }

    public void closeWithX() {
        click(CLOSE);
    }

    public void closeWithButton() {
        clickButton("Bağla");
    }

    public void submit() {
        if (hasButton("Yadda saxla")) {
            clickButton("Yadda saxla");
        } else {
            clickButton("Əlavə et");
        }
    }
}