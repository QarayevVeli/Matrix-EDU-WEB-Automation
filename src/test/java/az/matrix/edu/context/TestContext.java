package az.matrix.edu.context;

import az.matrix.edu.driver.DriverFactory;
import az.matrix.edu.pages.GridPage;
import az.matrix.edu.pages.TrainingFormModal;
import az.matrix.edu.pages.TrainingsPage;
import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class TestContext {

    private static final String LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private final List<String> createdTrainings = new ArrayList<>();
    private GridPage grid;
    private TrainingFormModal form;
    private String trainingName = "";
    private String trainingFilter = "";
    private String recordedValue = "";
    private int recordedCount;

    public WebDriver driver() {
        return DriverFactory.getDriver();
    }

    public GridPage grid() {
        return grid;
    }

    public void usePage(String key) {
        if (!"trainings".equals(key)) {
            throw new IllegalArgumentException("Naməlum səhifə: " + key);
        }
        this.grid = new TrainingsPage(driver());
    }

    public TrainingFormModal form() {
        return form;
    }

    public void form(TrainingFormModal modal) {
        this.form = modal;
    }

    public String trainingName() {
        return trainingName;
    }

    public void trainingName(String name) {
        this.trainingName = name;
    }

    public String trainingFilter() {
        return trainingFilter.isEmpty() ? trainingName : trainingFilter;
    }

    public void trainingFilter(String filter) {
        this.trainingFilter = filter;
    }

    public String recordedValue() {
        return recordedValue;
    }

    public void recordedValue(String value) {
        this.recordedValue = value;
    }

    public int recordedCount() {
        return recordedCount;
    }

    public void recordedCount(int count) {
        this.recordedCount = count;
    }

    public String uniqueName(String prefix) {
        return prefix + randomLetters(4) + randomDigits(4);
    }

    public String randomDigits(int length) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < length; index++) {
            builder.append(ThreadLocalRandom.current().nextInt(10));
        }
        return builder.toString();
    }

    public String randomLetters(int length) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < length; index++) {
            builder.append(LETTERS.charAt(ThreadLocalRandom.current().nextInt(LETTERS.length())));
        }
        return builder.toString();
    }

    public void registerTraining(String name) {
        createdTrainings.add(name);
    }

    public List<String> createdTrainings() {
        return createdTrainings;
    }
}