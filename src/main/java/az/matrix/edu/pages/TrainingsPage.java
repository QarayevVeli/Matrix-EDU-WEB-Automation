package az.matrix.edu.pages;

import org.openqa.selenium.WebDriver;

import java.util.Map;

public class TrainingsPage extends GridPage {

    public TrainingsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public String path() {
        return "/education/trainings";
    }

    @Override
    public String newButtonTitle() {
        return "Əlavə et";
    }

    @Override
    public String nameColumn() {
        return "Ad";
    }

    @Override
    protected Map<String, String> columns() {
        return Map.of("#", "id", "Ad", "name", "Təsvir", "description");
    }
}