package az.matrix.edu.steps;

import az.matrix.edu.context.TestContext;
import az.matrix.edu.pages.ConfirmDialog;
import az.matrix.edu.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;

public class CommonSteps {

    private static final Logger LOG = LogManager.getLogger(CommonSteps.class);
    private final TestContext context;

    public CommonSteps(TestContext context) {
        this.context = context;
    }

    @Given("the user is logged in to MatrixERP")
    public void theUserIsLoggedIn() {
        LOG.info("İstifadəçi sistemə daxil edilir");
        new LoginPage(context.driver()).open().loginWithConfiguredUser();
    }

    @Given("the user opens the {string} page")
    public void theUserOpensThePage(String key) {
        context.usePage(key);
        context.grid().open();
    }

    @Then("a success popup with message {string} is displayed")
    public void aSuccessPopupWithMessageIsDisplayed(String message) {
        ConfirmDialog dialog = new ConfirmDialog(context.driver());
        Assert.assertTrue(dialog.successAppearsWithin(10), "Uğur pəncərəsi görünmədi, faktiki: " + dialog.popupText());
        Assert.assertEquals(dialog.message(), message, "Uğur mesajı fərqlidir");
    }

    @Then("a success popup is displayed")
    public void aSuccessPopupIsDisplayed() {
        ConfirmDialog dialog = new ConfirmDialog(context.driver());
        Assert.assertTrue(dialog.successAppearsWithin(10), "Uğur pəncərəsi görünmədi, faktiki: " + dialog.popupText());
    }

    @Then("no success popup is displayed")
    public void noSuccessPopupIsDisplayed() {
        Assert.assertFalse(new ConfirmDialog(context.driver()).successAppearsWithin(3), "Uğur pəncərəsi görünməməli idi");
    }

    @Then("no error popup is displayed")
    public void noErrorPopupIsDisplayed() {
        ConfirmDialog dialog = new ConfirmDialog(context.driver());
        Assert.assertFalse(dialog.errorAppearsWithin(3), "Xəta pəncərəsi görünməməli idi: " + dialog.popupText());
    }

    @Then("an error popup with message {string} is displayed")
    public void anErrorPopupWithMessageIsDisplayed(String message) {
        ConfirmDialog dialog = new ConfirmDialog(context.driver());
        Assert.assertTrue(dialog.errorAppearsWithin(8), "Xəta pəncərəsi görünmədi");
        Assert.assertEquals(dialog.message(), message, "Xəta mesajı fərqlidir");
    }

    @When("the user closes the popup")
    public void theUserClosesThePopup() {
        new ConfirmDialog(context.driver()).acknowledge();
    }
}