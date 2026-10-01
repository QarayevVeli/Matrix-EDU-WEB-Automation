package az.matrix.edu.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

public class SidebarMenu extends BasePage {

    private static final By EDUCATION = By.xpath("//a[normalize-space(.)='Tədris']");
    private static final By TRAININGS = By.cssSelector("a[href='/education/trainings']");
    private static final By TEACHERS = By.cssSelector("a[href='/education/teachers']");
    private static final By SCHEDULE = By.cssSelector("a[href='/education/schedule']");

    public SidebarMenu(WebDriver driver) {
        super(driver);
    }

    public void expandEducation() {
        if (!isDisplayed(TRAININGS)) {
            log.info("Tədris menyusu açılır");
            click(EDUCATION);
        }
        visible(TRAININGS);
    }

    public List<String> educationItems() {
        expandEducation();
        List<String> items = new ArrayList<>();
        items.add(contentOf(TRAININGS));
        items.add(contentOf(TEACHERS));
        items.add(contentOf(SCHEDULE));
        return items;
    }

    public void openSection(String section) {
        expandEducation();
        log.info("Tədris bölməsi açılır: {}", section);
        switch (section) {
            case "trainings" -> click(TRAININGS);
            case "teachers" -> click(TEACHERS);
            case "schedule" -> click(SCHEDULE);
            default -> throw new IllegalArgumentException("Naməlum bölmə: " + section);
        }
    }

    public boolean urlContains(String path) {
        return wait.until(ExpectedConditions.urlContains(path));
    }

    public boolean errorPageDisplayed() {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(4)).until(ExpectedConditions.urlContains("/miscellaneous/error"));
            return true;
        } catch (org.openqa.selenium.TimeoutException e) {
            return false;
        }
    }

    public String errorPageText() {
        return appearsWithin(By.xpath("//*[contains(.,'Səhifə Tapılmadı')]"), 3) ? "Səhifə Tapılmadı" : "";
    }

    public void clickHomeLink() {
        click(By.xpath("//a[contains(.,'Ana səhifəyə qayıt')]"));
    }

    public boolean homeLinkDisplayed() {
        return isDisplayed(By.xpath("//a[contains(.,'Ana səhifəyə qayıt')]"));
    }

    public void openAddress(String path) {
        driver.get(az.matrix.edu.config.ConfigReader.get("base.url") + path);
    }
}