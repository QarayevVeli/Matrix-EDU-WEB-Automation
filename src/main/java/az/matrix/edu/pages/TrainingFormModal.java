package az.matrix.edu.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TrainingFormModal extends ModalPage {

    private static final By NAME = By.cssSelector(MODAL + " input[id$='-name']");
    private static final By DESCRIPTION = By.cssSelector(MODAL + " textarea[id$='-description']");

    public TrainingFormModal(WebDriver driver) {
        super(driver);
    }

    public void enterName(String name) {
        type(NAME, name);
    }

    public void enterDescription(String description) {
        type(DESCRIPTION, description);
    }

    public void pressEnterInName() {
        log.info("Ad sahəsində Enter basılır");
        visible(NAME).sendKeys(org.openqa.selenium.Keys.ENTER);
    }

    public String name() {
        return valueOf(NAME);
    }

    public String description() {
        return valueOf(DESCRIPTION);
    }

    public int nameMaxLength() {
        String value = attributeOf(NAME, "maxlength");
        return value.isEmpty() ? -1 : Integer.parseInt(value);
    }

    public int descriptionMaxLength() {
        String value = attributeOf(DESCRIPTION, "maxlength");
        return value.isEmpty() ? -1 : Integer.parseInt(value);
    }
}