package az.matrix.edu.steps;

import az.matrix.edu.context.TestContext;
import az.matrix.edu.pages.SidebarMenu;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.List;

public class MenuSteps {

    private final TestContext context;

    public MenuSteps(TestContext context) {
        this.context = context;
    }

    private SidebarMenu menu() {
        return new SidebarMenu(context.driver());
    }

    @Then("the Education menu contains {string}")
    public void theEducationMenuContains(String expected) {
        List<String> items = menu().educationItems();
        for (String name : expected.split(",")) {
            Assert.assertTrue(items.stream().anyMatch(item -> item.contains(name.trim())),
                    "Tədris menyusunda '" + name.trim() + "' tapılmadı, mövcud: " + items);
        }
    }

    @When("the user opens the {string} page from the Education menu")
    public void theUserOpensThePageFromTheEducationMenu(String key) {
        menu().openSection(key);
    }

    @When("the user opens the address {string}")
    public void theUserOpensTheAddress(String path) {
        menu().openAddress(path);
    }

    @Then("the current page url contains {string}")
    public void theCurrentPageUrlContains(String path) {
        Assert.assertTrue(menu().urlContains(path), "URL '" + path + "' ehtiva etmir, faktiki: " + context.driver().getCurrentUrl());
    }

    @Then("the page is not the not found page")
    public void thePageIsNotTheNotFoundPage() {
        Assert.assertFalse(menu().errorPageDisplayed(), "'Səhifə Tapılmadı' xəta səhifəsi açıldı: " + context.driver().getCurrentUrl());
    }

    @Then("the not found page is displayed")
    public void theNotFoundPageIsDisplayed() {
        Assert.assertTrue(menu().errorPageDisplayed(), "Xəta səhifəsi açılmadı");
        Assert.assertEquals(menu().errorPageText(), "Səhifə Tapılmadı", "Xəta mətni fərqlidir");
    }

    @Then("the not found page offers the link back to the home page")
    public void theNotFoundPageOffersTheLinkBackToTheHomePage() {
        Assert.assertTrue(menu().homeLinkDisplayed(), "Ana səhifəyə qayıt linki yoxdur");
    }

    @When("the user clicks the link back to the home page")
    public void theUserClicksTheLinkBackToTheHomePage() {
        menu().clickHomeLink();
    }

}
