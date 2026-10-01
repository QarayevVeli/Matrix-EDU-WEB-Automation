package az.matrix.edu.steps;

import az.matrix.edu.context.TestContext;
import az.matrix.edu.pages.GridPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class GridSteps {

    private final TestContext context;

    public GridSteps(TestContext context) {
        this.context = context;
    }

    private GridPage grid() {
        return context.grid();
    }

    @Then("the grid columns start with {string}")
    public void theGridColumnsStartWith(String expected) {
        List<String> headers = grid().headerTitles();
        for (String title : expected.split(",")) {
            boolean found = headers.stream().anyMatch(header -> header.startsWith(title.trim()));
            Assert.assertTrue(found, "Sütun tapılmadı: " + title.trim() + ", mövcud: " + headers);
        }
    }

    @Then("the grid lists {int} rows on the first page")
    public void theGridListsRows(int expected) {
        Assert.assertEquals(grid().rowCount(), Math.min(expected, grid().total()), "Sətir sayı fərqlidir");
    }

    @Then("the first row offers the actions {string}")
    public void theFirstRowOffersTheActions(String actions) {
        Assert.assertEquals(grid().actionTitles(0), Arrays.asList(actions.split(",")), "Əməliyyat ikonları fərqlidir");
    }

    @Then("the export button is displayed")
    public void theExportButtonIsDisplayed() {
        Assert.assertTrue(grid().isExportDisplayed(), "Excel ixrac düyməsi görünmür");
    }

    @When("the user filters the {string} column by {string}")
    public void theUserFiltersTheColumnBy(String column, String text) {
        grid().filterBy(column, text);
    }

    @When("the user filters the {string} column by a text of {int} characters")
    public void theUserFiltersTheColumnByALongText(String column, int length) {
        grid().filterBy(column, "Q".repeat(length));
    }

    @When("the user selects {string} in the {string} filter")
    public void theUserSelectsInTheFilter(String option, String column) {
        grid().selectFilter(column, option);
    }

    @Then("the grid shows at least {int} record")
    public void theGridShowsAtLeastRecords(int minimum) {
        Assert.assertTrue(grid().total() >= minimum, "Nəticə tapılmadı: " + grid().resultInfo());
    }

    @Then("the grid shows no records")
    public void theGridShowsNoRecords() {
        Assert.assertEquals(grid().rowCount(), 0, "Sətir gözlənilmirdi: " + grid().resultInfo());
        Assert.assertEquals(grid().total(), 0, "Nəticə sayı 0 olmalıdır");
    }

    @Then("every row contains {string} in the {string} column ignoring case")
    public void everyRowContainsIgnoringCase(String text, String column) {
        List<String> values = grid().columnValues(column);
        Assert.assertFalse(values.isEmpty(), "Cədvəl boşdur");
        for (String value : values) {
            Assert.assertTrue(value.toLowerCase().contains(text.toLowerCase()), "'" + value + "' sətri '" + text + "' ehtiva etmir");
        }
    }

    @Then("every row has {string} in the {string} column")
    public void everyRowHasInTheColumn(String expected, String column) {
        List<String> values = grid().columnValues(column);
        Assert.assertFalse(values.isEmpty(), "Cədvəl boşdur");
        for (String value : values) {
            Assert.assertEquals(value, expected, "Sütun dəyəri fərqlidir, cədvəl: " + grid().resultInfo());
        }
    }

    @When("the user sorts by the {string} column")
    public void theUserSortsByTheColumn(String column) {
        grid().sortBy(column);
    }

    @Then("the {string} column is sorted in ascending order")
    public void theColumnIsSortedAscending(String column) {
        assertSorted(column, true);
    }

    @Then("the {string} column is sorted in descending order")
    public void theColumnIsSortedDescending(String column) {
        assertSorted(column, false);
    }

    @Then("the {string} column header shows a sort number")
    public void theColumnHeaderShowsASortNumber(String column) {
        Assert.assertTrue(grid().headerText(column).matches(".+\\s\\d+$"), "Sıralama nömrəsi yoxdur: " + grid().headerText(column));
    }

    private void assertSorted(String column, boolean ascending) {
        List<String> values = grid().columnValues(column);
        Assert.assertTrue(values.size() > 1, "Sıralamanı yoxlamaq üçün kifayət qədər sətir yoxdur");
        boolean numeric = values.stream().allMatch(value -> value.replace("#", "").replace(",", "").matches("-?\\d+(\\.\\d+)?"));
        List<String> expected = new ArrayList<>(values);
        Comparator<String> comparator = numeric
                ? Comparator.comparingDouble(value -> Double.parseDouble(value.replace("#", "").replace(",", "")))
                : String.CASE_INSENSITIVE_ORDER;
        expected.sort(ascending ? comparator : comparator.reversed());
        Assert.assertEquals(values, expected, "Sıralama düzgün deyil: " + values);
    }

    @When("the user goes to the next page")
    public void theUserGoesToTheNextPage() {
        grid().nextPage();
    }

    @When("the user goes to the previous page")
    public void theUserGoesToThePreviousPage() {
        grid().previousPage();
    }

    @Then("the results info starts with {string}")
    public void theResultsInfoStartsWith(String prefix) {
        Assert.assertTrue(grid().resultInfo().startsWith(prefix), "Nəticə məlumatı fərqlidir: " + grid().resultInfo());
    }

    @When("the user selects the page size {int}")
    public void theUserSelectsThePageSize(int size) {
        grid().selectPageSize(size);
    }

    @Then("the grid shows no more than {int} rows and the selected page size is {int}")
    public void theGridShowsNoMoreThanRows(int limit, int size) {
        Assert.assertEquals(grid().selectedPageSize(), String.valueOf(size), "Seçilmiş səhifə ölçüsü fərqlidir");
        Assert.assertEquals(grid().rowCount(), Math.min(limit, grid().total()), "Sətir sayı fərqlidir: " + grid().resultInfo());
    }

    @When("the user notes the total number of records")
    public void theUserNotesTheTotalNumberOfRecords() {
        context.recordedCount(grid().total());
    }

    @When("the user refreshes the grid")
    public void theUserRefreshesTheGrid() {
        grid().refresh();
    }

    @When("the user resets the filters")
    public void theUserResetsTheFilters() {
        grid().resetFilters();
    }

    @Then("the {string} filter still contains {string}")
    public void theFilterStillContains(String column, String text) {
        Assert.assertEquals(grid().filterValue(column), text, "Filtr dəyəri dəyişib");
    }

    @Then("the {string} filter is empty")
    public void theFilterIsEmpty(String column) {
        Assert.assertEquals(grid().filterValue(column), "", "Filtr təmizlənməyib");
    }

    @Then("the total number of records equals the noted number")
    public void theTotalEqualsTheNotedNumber() {
        Assert.assertEquals(grid().total(), context.recordedCount(), "Qeydlərin sayı bərpa olunmadı");
    }
}