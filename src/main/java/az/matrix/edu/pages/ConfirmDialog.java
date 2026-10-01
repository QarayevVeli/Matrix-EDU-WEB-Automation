package az.matrix.edu.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ConfirmDialog extends BasePage {

    private static final By POPUP = By.cssSelector(".swal2-popup");
    private static final By TITLE = By.cssSelector(".swal2-popup .swal2-title");
    private static final By MESSAGE = By.cssSelector(".swal2-popup .swal2-html-container");
    private static final By CONFIRM = By.cssSelector(".swal2-popup .swal2-confirm");
    private static final By CANCEL = By.cssSelector(".swal2-popup .swal2-cancel");
    private static final String SUCCESS_TITLE = "Uğurlu!";
    private static final String ERROR_TITLE = "Xəta!";

    public ConfirmDialog(WebDriver driver) {
        super(driver);
    }

    public void waitVisible() {
        visible(POPUP);
    }

    public boolean popupAppearsWithin(int seconds) {
        return appearsWithin(POPUP, seconds);
    }

    public boolean successAppearsWithin(int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(d -> isDisplayed(POPUP) && textOf(TITLE).equals(SUCCESS_TITLE));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean errorAppearsWithin(int seconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(d -> isDisplayed(POPUP) && textOf(TITLE).equals(ERROR_TITLE));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String title() {
        return textOf(TITLE);
    }

    public String message() {
        return textOf(MESSAGE);
    }

    public void confirm() {
        click(CONFIRM);
    }

    public void acknowledge() {
        click(CONFIRM);
        waitInvisible(POPUP);
    }

    public void dismissIfDisplayed() {
        if (isDisplayed(POPUP)) {
            log.warn("Xəta pəncərəsi bağlanır: {}", textOf(POPUP).replace("\n", " "));
            click(CONFIRM);
            waitInvisible(POPUP);
        }
    }

    public String popupText() {
        return isDisplayed(POPUP) ? textOf(POPUP).replace("\n", " ") : "";
    }

    public void cancel() {
        click(CANCEL);
        waitInvisible(POPUP);
    }
}
