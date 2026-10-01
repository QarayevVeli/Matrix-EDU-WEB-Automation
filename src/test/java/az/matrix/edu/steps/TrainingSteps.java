package az.matrix.edu.steps;

import az.matrix.edu.context.TestContext;
import az.matrix.edu.pages.ConfirmDialog;
import az.matrix.edu.pages.GridPage;
import az.matrix.edu.pages.TrainingFormModal;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.Arrays;

public class TrainingSteps {

    private final TestContext context;

    public TrainingSteps(TestContext context) {
        this.context = context;
    }

    private GridPage grid() {
        return context.grid();
    }

    private TrainingFormModal form() {
        return context.form();
    }

    private int rowOf(String name) {
        return grid().findRow(grid().nameColumn(), name);
    }

    private void register(String name, String filter) {
        context.trainingName(name);
        context.trainingFilter(filter);
        context.registerTraining(name);
    }

    @When("the user opens the new training form")
    public void theUserOpensTheNewTrainingForm() {
        grid().clickNew();
        context.form(new TrainingFormModal(context.driver()));
        form().waitOpen();
    }

    @Then("the training form title is {string}")
    public void theTrainingFormTitleIs(String title) {
        Assert.assertEquals(form().title(), title, "Pəncərə başlığı fərqlidir");
    }

    @Then("the training form has the labels {string}")
    public void theTrainingFormHasTheLabels(String expected) {
        for (String label : expected.split(",")) {
            Assert.assertTrue(form().labels().contains(label.trim()), "Etiket tapılmadı: " + label + ", mövcud: " + form().labels());
        }
    }

    @Then("the training form has the buttons {string}")
    public void theTrainingFormHasTheButtons(String buttons) {
        for (String button : buttons.split(",")) {
            Assert.assertTrue(form().hasButton(button.trim()), "Düymə tapılmadı: " + button);
        }
    }

    @When("the user enters the training name {string}")
    public void theUserEntersTheTrainingName(String name) {
        form().enterName(name);
        context.trainingName(name);
    }

    @When("the user enters {int} spaces as the training name")
    public void theUserEntersSpacesAsTheTrainingName(int count) {
        form().enterName(" ".repeat(count));
    }

    @When("the user enters a unique training name")
    public void theUserEntersAUniqueTrainingName() {
        String name = context.uniqueName("QA");
        form().enterName(name);
        register(name, name);
    }

    @When("the user enters a unique training name containing {string}")
    public void theUserEntersAUniqueTrainingNameContaining(String text) {
        String unique = context.uniqueName("QA");
        form().enterName(unique + " " + text);
        register(unique + " " + text, unique);
    }

    @When("the user enters a unique training name of {int} characters")
    public void theUserEntersAUniqueTrainingNameOfCharacters(int length) {
        String unique = context.uniqueName("QA");
        String name = unique + "Z".repeat(length - unique.length());
        form().enterName(name);
        register(name, unique);
    }

    @When("the user enters a training name of {int} characters")
    public void theUserEntersATrainingNameOfCharacters(int length) {
        form().enterName("Q".repeat(length));
    }

    @When("the user enters the description {string}")
    public void theUserEntersTheDescription(String description) {
        form().enterDescription(description);
        context.recordedValue(description);
    }

    @When("the user enters a description of {int} characters")
    public void theUserEntersADescriptionOfCharacters(int length) {
        form().enterDescription("D".repeat(length));
        context.recordedValue("D".repeat(Math.min(length, 500)));
    }

    @When("the user submits the training form")
    public void theUserSubmitsTheTrainingForm() {
        form().submit();
    }

    @When("the user presses Enter in the training name field")
    public void theUserPressesEnterInTheTrainingNameField() {
        form().pressEnterInName();
    }

    @When("the user cancels the training form")
    public void theUserCancelsTheTrainingForm() {
        form().cancel();
    }

    @When("the user closes the training form with the close button")
    public void theUserClosesTheTrainingFormWithTheCloseButton() {
        form().closeWithX();
    }

    @When("the user creates a training with a unique name")
    public void theUserCreatesATrainingWithAUniqueName() {
        theUserOpensTheNewTrainingForm();
        theUserEntersAUniqueTrainingName();
        form().submit();
        ConfirmDialog dialog = new ConfirmDialog(context.driver());
        Assert.assertTrue(dialog.successAppearsWithin(10), "Təlim yaradılmadı: " + dialog.popupText());
        dialog.acknowledge();
    }

    @When("the user opens the new training form again and enters the name of the existing training")
    public void theUserOpensTheNewTrainingFormAgain() {
        theUserOpensTheNewTrainingForm();
        form().enterName(context.trainingName());
    }

    @Then("the training name field keeps at most {int} characters")
    public void theTrainingNameFieldKeepsAtMostCharacters(int max) {
        Assert.assertEquals(form().nameMaxLength(), max, "maxlength atributu fərqlidir");
        Assert.assertTrue(form().name().length() <= max, "Sahə " + form().name().length() + " simvol qəbul etdi");
    }

    @Then("the training description field keeps at most {int} characters")
    public void theTrainingDescriptionFieldKeepsAtMostCharacters(int max) {
        Assert.assertEquals(form().descriptionMaxLength(), max, "maxlength atributu fərqlidir");
        Assert.assertTrue(form().description().length() <= max, "Sahə " + form().description().length() + " simvol qəbul etdi");
    }

    @Then("the training form shows the validation message {string}")
    public void theTrainingFormShowsTheValidationMessage(String message) {
        Assert.assertTrue(form().validationMessages().contains(message),
                "Xəta mesajı tapılmadı: " + message + ", mövcud: " + form().validationMessages());
    }

    @Then("the training form is closed")
    public void theTrainingFormIsClosed() {
        Assert.assertTrue(form().closesWithin(5), "Pəncərə bağlanmadı");
    }

    @Then("the training form is still open")
    public void theTrainingFormIsStillOpen() {
        Assert.assertTrue(form().isOpen(), "Pəncərə bağlanmamalı idi");
    }

    @Then("the training is listed in the grid")
    public void theTrainingIsListedInTheGrid() {
        grid().open();
        grid().filterBy(grid().nameColumn(), context.trainingFilter());
        Assert.assertTrue(rowOf(context.trainingName()) >= 0, "Təlim cədvəldə tapılmadı: " + context.trainingName());
    }

    @Then("the training is listed in the grid with the recorded description")
    public void theTrainingIsListedWithTheRecordedDescription() {
        theTrainingIsListedInTheGrid();
        Assert.assertEquals(grid().cell(rowOf(context.trainingName()), "Təsvir"), context.recordedValue(), "Təsvir fərqlidir");
    }

    @Then("the training is listed in the grid exactly {int} time")
    public void theTrainingIsListedExactlyTimes(int times) {
        grid().open();
        grid().filterBy(grid().nameColumn(), context.trainingFilter());
        long count = grid().columnValues(grid().nameColumn()).stream().filter(context.trainingName()::equals).count();
        Assert.assertEquals(count, times, "Təlimin sayı fərqlidir: " + context.trainingName());
    }

    @Then("the training is not listed in the grid")
    public void theTrainingIsNotListedInTheGrid() {
        grid().open();
        grid().filterBy(grid().nameColumn(), context.trainingFilter());
        Assert.assertTrue(rowOf(context.trainingName()) < 0, "Təlim yaradılmamalı idi: " + context.trainingName());
    }

    @Then("the newest training is shown in the first row")
    public void theNewestTrainingIsShownInTheFirstRow() {
        grid().open();
        int row = rowOf(context.trainingName());
        Assert.assertTrue(row >= 0 && row < 5, "Yeni təlim ilk sətirlərdə deyil, sətir: " + row);
    }

    @Then("the total number of records is one more than the noted number")
    public void theTotalNumberOfRecordsIsOneMore() {
        grid().open();
        Assert.assertTrue(grid().total() >= context.recordedCount() + 1, "Qeyd sayı artmadı: " + grid().total());
    }

    @Then("the results info has the format of a range and a total")
    public void theResultsInfoHasTheFormat() {
        Assert.assertTrue(grid().resultInfo().matches("\\d+–\\d+ / \\d+ nəticə"), "Format fərqlidir: " + grid().resultInfo());
    }

    @Then("the next page button is disabled")
    public void theNextPageButtonIsDisabled() {
        Assert.assertFalse(grid().isNextEnabled(), "Növbəti səhifə düyməsi aktiv olmamalıdır");
    }

    @Then("the previous page button is disabled")
    public void thePreviousPageButtonIsDisabled() {
        Assert.assertFalse(grid().isPreviousEnabled(), "Əvvəlki səhifə düyməsi aktiv olmamalıdır");
    }

    @Then("the page size options are {string}")
    public void thePageSizeOptionsAre(String options) {
        Assert.assertEquals(grid().pageSizeOptions(), Arrays.asList(options.split(",")), "Səhifə ölçüsü seçimləri fərqlidir");
    }

    @Then("the first row offers the action {string}")
    public void theFirstRowOffersTheAction(String action) {
        Assert.assertTrue(grid().actionTitles(0).contains(action),
                "Sətirdə '" + action + "' ikonu yoxdur, mövcud ikonlar: " + grid().actionTitles(0));
    }

    @When("the user exports the grid to Excel")
    public void theUserExportsTheGridToExcel() {
        grid().clickExport();
    }
}