package az.matrix.edu.pages;

import az.matrix.edu.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class GridPage extends BasePage {

    private static final By ROWS = By.cssSelector(".ag-center-cols-container .ag-row");
    private static final By HEADERS = By.cssSelector(".ag-header-row-column .ag-header-cell");
    private static final By LOADING = By.cssSelector(".ag-overlay-loading-center");
    private static final By NO_ROWS = By.cssSelector(".ag-overlay-no-rows-center");
    private static final By RESULT_INFO = By.xpath("//*[contains(normalize-space(.),'nəticə') and not(*)]");
    private static final By PAGE_SIZE = By.xpath("//select[option[normalize-space(.)='100']]");
    private static final By RESET = By.cssSelector("button[title='Filtrləri sıfırla']");
    private static final By REFRESH = By.cssSelector("button[title='Yenilə']");
    private static final By EXPORT = By.cssSelector("button[title='Excel-ə ixrac et']");
    private static final By NEXT = By.cssSelector("button[title='Növbəti səhifə']");
    private static final By PREVIOUS = By.cssSelector("button[title='Əvvəlki səhifə']");

    protected GridPage(WebDriver driver) {
        super(driver);
    }

    public abstract String path();

    public abstract String newButtonTitle();

    public abstract String nameColumn();

    protected abstract Map<String, String> columns();

    public GridPage open() {
        String url = ConfigReader.get("base.url") + path();
        log.info("Səhifə açılır: {}", url);
        driver.get(url);
        waitUntilLoaded();
        return this;
    }

    public void waitUntilLoaded() {
        wait.until(d -> !all(RESULT_INFO).isEmpty() && all(LOADING).stream().noneMatch(WebElement::isDisplayed));
        int previous = -1;
        for (int attempt = 0; attempt < 8; attempt++) {
            int current = rowCount();
            if (current == previous) {
                break;
            }
            previous = current;
            pause(400);
        }
        log.info("Cədvəl yükləndi: {}", resultInfo());
    }

    public List<String> headerTitles() {
        scrollGridTo(0);
        return stable(() -> all(HEADERS).stream()
                .map(header -> header.getAttribute("textContent").trim())
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList()));
    }

    public String columnId(String title) {
        String id = columns().get(title);
        if (id == null) {
            throw new IllegalArgumentException("Sütun tapılmadı: " + title);
        }
        return id;
    }

    private By headerCell(String columnId) {
        return By.cssSelector(".ag-header-row-column .ag-header-cell[col-id='" + columnId + "']");
    }

    private void scrollGridTo(int position) {
        ((JavascriptExecutor) driver).executeScript(
                "const v=document.querySelector('.ag-center-cols-viewport');if(v){v.scrollLeft=arguments[0];}", position);
    }

    private String reveal(String title) {
        String columnId = columnId(title);
        if (!all(headerCell(columnId)).isEmpty()) {
            return columnId;
        }
        for (int position : new int[]{0, 400, 800, 1200, 1600, 5000}) {
            scrollGridTo(position);
            pause(250);
            if (!all(headerCell(columnId)).isEmpty()) {
                return columnId;
            }
        }
        throw new IllegalStateException("Sütun görünmədi: " + title);
    }

    public int rowCount() {
        return all(ROWS).size();
    }

    public String resultInfo() {
        return stable(() -> present(RESULT_INFO).getAttribute("textContent").trim());
    }

    public int total() {
        String info = resultInfo();
        Matcher matcher = Pattern.compile("/\\s*(\\d+)").matcher(info);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        Matcher zero = Pattern.compile("^\\s*(\\d+)").matcher(info);
        if (zero.find()) {
            return Integer.parseInt(zero.group(1));
        }
        throw new IllegalStateException("Nəticə sayı oxunmadı: " + info);
    }

    public String cell(int row, String title) {
        String columnId = reveal(title);
        By cell = By.cssSelector(".ag-center-cols-container .ag-row[row-index='" + row + "'] div[col-id='" + columnId + "']");
        return contentOf(cell);
    }

    public List<String> columnValues(String title) {
        int count = rowCount();
        List<String> values = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            values.add(cell(index, title));
        }
        return values;
    }

    public int findRow(String title, String text) {
        int count = rowCount();
        for (int index = 0; index < count; index++) {
            if (cell(index, title).contains(text)) {
                return index;
            }
        }
        return -1;
    }

    public void clickAction(int row, String title) {
        log.info("Sətir {} üçün əməliyyat: {}", row, title);
        click(By.cssSelector(".ag-pinned-right-cols-container .ag-row[row-index='" + row + "'] a[title='" + title + "']"));
    }

    public List<String> actionTitles(int row) {
        return stable(() -> all(By.cssSelector(".ag-pinned-right-cols-container .ag-row[row-index='" + row + "'] a[title]")).stream()
                .map(icon -> icon.getAttribute("title"))
                .collect(Collectors.toList()));
    }

    public void clickNew() {
        click(By.cssSelector("button[title='" + newButtonTitle() + "']"));
    }

    public void clickExport() {
        click(EXPORT);
    }

    public boolean isExportDisplayed() {
        return isDisplayed(EXPORT);
    }

    public void filterBy(String column, String text) {
        log.info("Filtr: {} = '{}'", column, text);
        String before = snapshot();
        reveal(column);
        By input = By.cssSelector("input[aria-label='" + column + " Filter Input']");
        type(input, text);
        visible(input).sendKeys(Keys.ENTER);
        waitForChange(before);
        waitUntilLoaded();
    }

    public void selectFilter(String column, String option) {
        log.info("Seçim filtri: {} = '{}'", column, option);
        String before = snapshot();
        By select = By.cssSelector("div[col-id='" + reveal(column) + "'] select");
        new Select(visible(select)).selectByVisibleText(option);
        waitForChange(before);
        waitUntilLoaded();
    }

    public List<String> selectFilterOptions(String column) {
        return optionTexts(By.cssSelector("div[col-id='" + reveal(column) + "'] select"));
    }

    public String filterValue(String column) {
        reveal(column);
        return valueOf(By.cssSelector("input[aria-label='" + column + " Filter Input']"));
    }

    public void resetFilters() {
        String before = snapshot();
        click(RESET);
        waitForChange(before);
        waitUntilLoaded();
    }

    public void refresh() {
        click(REFRESH);
        pause(500);
        waitUntilLoaded();
    }

    public void selectPageSize(int size) {
        String before = snapshot();
        new Select(visible(PAGE_SIZE)).selectByVisibleText(String.valueOf(size));
        waitForChange(before);
        waitUntilLoaded();
    }

    public String selectedPageSize() {
        return new Select(visible(PAGE_SIZE)).getFirstSelectedOption().getText().trim();
    }

    public List<String> pageSizeOptions() {
        return optionTexts(PAGE_SIZE);
    }

    public void sortBy(String title) {
        log.info("Sıralama: {}", title);
        String before = snapshot();
        click(By.cssSelector(".ag-header-row-column .ag-header-cell[col-id='" + reveal(title) + "'] .ag-header-cell-label"));
        waitForChange(before);
        pause(600);
        waitUntilLoaded();
    }

    public String headerText(String title) {
        return contentOf(headerCell(reveal(title))).replaceAll("\\s+", " ");
    }

    public void nextPage() {
        String before = snapshot();
        click(NEXT);
        waitForChange(before);
        waitUntilLoaded();
    }

    public void previousPage() {
        String before = snapshot();
        click(PREVIOUS);
        waitForChange(before);
        waitUntilLoaded();
    }

    public boolean isNextEnabled() {
        return present(NEXT).isEnabled();
    }

    public boolean isPreviousEnabled() {
        return present(PREVIOUS).isEnabled();
    }

    public boolean noRowsOverlayDisplayed() {
        return isDisplayed(NO_ROWS);
    }

    private String snapshot() {
        return resultInfo() + "|" + rowCount() + "|" + firstRowText();
    }

    private String firstRowText() {
        return stable(() -> {
            List<WebElement> rows = all(ROWS);
            return rows.isEmpty() ? "" : rows.get(0).getAttribute("textContent");
        });
    }

    private void waitForChange(String before) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(4)).until(d -> !snapshot().equals(before));
        } catch (TimeoutException e) {
            log.info("Nəticə dəyişmədi, davam edilir");
        }
    }
}