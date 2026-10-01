package az.matrix.edu.steps;

import az.matrix.edu.config.ConfigReader;
import az.matrix.edu.context.TestContext;
import az.matrix.edu.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class LoginSteps {

    private static final Logger LOG = LogManager.getLogger(LoginSteps.class);
    private final TestContext context;
    private LoginPage loginPage;

    public LoginSteps(TestContext context) {
        this.context = context;
    }

    @Given("the login page is opened")
    public void theLoginPageIsOpened() {
        loginPage = new LoginPage(context.driver()).open();
    }

    @When("the user logs in with the configured credentials")
    public void theUserLogsInWithTheConfiguredCredentials() {
        loginPage.login(ConfigReader.get("username"), ConfigReader.get("password"));
    }

    @When("the user logs in with the configured username and password {string}")
    public void theUserLogsInWithPassword(String password) {
        loginPage.login(ConfigReader.get("username"), password);
    }

    @When("the user submits the login form without credentials")
    public void theUserSubmitsTheLoginFormWithoutCredentials() {
        loginPage.login("", "");
    }

    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsInWithUsernameAndPassword(String username, String password) {
        loginPage.login(username, password);
    }

    @When("the user logs in with a username of {int} characters and password {string}")
    public void theUserLogsInWithALongUsername(int length, String password) {
        loginPage.login("a".repeat(length), password);
    }

    @When("the user logs in with an empty username and the configured password")
    public void theUserLogsInWithAnEmptyUsername() {
        loginPage.login("", ConfigReader.get("password"));
    }

    @When("the user logs in with the configured username and an empty password")
    public void theUserLogsInWithAnEmptyPassword() {
        loginPage.login(ConfigReader.get("username"), "");
    }

    @When("the user enters the configured credentials and presses Enter")
    public void theUserEntersTheConfiguredCredentialsAndPressesEnter() {
        loginPage.enterUsername(ConfigReader.get("username"));
        loginPage.enterPassword(ConfigReader.get("password"));
        loginPage.pressEnterInPassword();
    }

    @When("the user opens {string} without a session")
    public void theUserOpensWithoutASession(String path) {
        loginPage.openWithoutSession(path);
    }

    @When("the user refreshes the browser")
    public void theUserRefreshesTheBrowser() {
        LOG.info("Brauzer yenilənir");
        context.driver().navigate().refresh();
    }

    @When("the user navigates back in the browser")
    public void theUserNavigatesBack() {
        LOG.info("Brauzerdə geri qayıdılır");
        context.driver().navigate().back();
    }

    @When("the user navigates forward in the browser")
    public void theUserNavigatesForward() {
        LOG.info("Brauzerdə irəli keçilir");
        context.driver().navigate().forward();
    }

    @Then("the forgot password link is displayed")
    public void theForgotPasswordLinkIsDisplayed() {
        Assert.assertTrue(loginPage.isForgotPasswordDisplayed(), "Şifrəni unutdun linki görünmür");
    }

    @Then("the dashboard is displayed")
    public void theDashboardIsDisplayed() {
        Assert.assertTrue(loginPage.isDashboardOpen(), "Dashboard açılmadı");
    }

    @Then("the user stays on the login page")
    public void theUserStaysOnTheLoginPage() {
        Assert.assertFalse(loginPage.isDashboardOpenWithin(4), "İstifadəçi dashboard-a keçməməli idi");
        Assert.assertTrue(loginPage.isLoginFormDisplayed(), "Login forması görünmür");
    }

    @Then("the user is redirected to the login page")
    public void theUserIsRedirectedToTheLoginPage() {
        Assert.assertTrue(loginPage.redirectedToLogin(), "Login səhifəsinə yönləndirilmədi: " + context.driver().getCurrentUrl());
    }

    @Then("the user is still signed in")
    public void theUserIsStillSignedIn() {
        String url = context.driver().getCurrentUrl();
        Assert.assertFalse(url.contains("/authentication/login"), "Sessiya itdi, cari URL: " + url);
    }
}