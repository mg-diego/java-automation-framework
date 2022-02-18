import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BaseTest {

    private WebDriver driver;

    @BeforeEach
    public void beforeTest() {
        System.setProperty("webdriver.chrome.driver","C:\\temp\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    @AfterEach
    public void afterTest() {
        if(null != driver) {
            driver.close();
            driver.quit();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}
