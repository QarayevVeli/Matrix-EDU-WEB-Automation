package az.matrix.edu.pages;

import az.matrix.edu.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class LoginPage extends BasePage {

    private static final By USERNAME = By.cssSelector("input[placeholder='İstifadəçi adı']");
    private static final By PASSWORD = By.cssSelector("input[type='password']");
    private static final By SUBMIT = By.xpath("//button[normalize-space(.)='Daxil ol']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        String url = ConfigReader.get("base.url") + "/pages/authentication/login";
        log.info("Login səhifəsi açılır: {}", url);
        driver.get(url);
        visible(USERNAME);
        return this;
    }

    public void login(String username, String password) {
        log.info("Giriş edilir, istifadəçi: {}", username);
        if (!username.isEmpty()) {
            type(USERNAME, username);
        }
        if (!password.isEmpty()) {
            typeSecret(PASSWORD, password);
        }
        click(SUBMIT);
    }

    public void enterUsername(String value) {
        type(USERNAME, value);
    }

    public void enterPassword(String value) {
        typeSecret(PASSWORD, value);
    }

    public void pressEnterInPassword() {
        log.info("Şifrə sahəsində Enter basılır");
        visible(PASSWORD).sendKeys(Keys.ENTER);
    }

    public void openWithoutSession(String path) {
        String url = ConfigReader.get("base.url") + path;
        log.info("Sessiyasız açılır: {}", url);
        driver.get(url);
    }

    public boolean redirectedToLogin() {
        try {
            wait.until(ExpectedConditions.urlContains("/authentication/login"));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void loginWithConfiguredUser() {
        login(ConfigReader.get("username"), ConfigReader.get("password"));
        waitForDashboard();
    }

    public void waitForDashboard() {
        wait.until(ExpectedConditions.urlContains("/dashboard"));
        log.info("Dashboard açıldı");
    }

    public boolean isDashboardOpen() {
        try {
            waitForDashboard();
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isDashboardOpenWithin(int seconds) {
        try {
            new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.urlContains("/dashboard"));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isForgotPasswordDisplayed() {
        return isDisplayed(By.xpath("//a[contains(normalize-space(.),'Şifrəni unutdun')]"));
    }

    public boolean isLoginFormDisplayed() {
        return isDisplayed(USERNAME) && driver.getCurrentUrl().contains("/authentication/login");
    }

    public String errorText() {
        return appearsWithin(By.cssSelector(".swal2-popup, .toast-message, .alert, .text-danger"), 4)
                ? contentOf(By.cssSelector(".swal2-popup, .toast-message, .alert, .text-danger"))
                : "";
    }
}