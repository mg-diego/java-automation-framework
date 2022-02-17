import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestingClass {

    private WebDriver driver;

    @Before
    public void beforeTest()
    {
        System.setProperty("webdriver.chrome.driver","C:\\temp\\chromedriver.exe");
        driver = new ChromeDriver();
    }

    @After
    public void afterTest()
    {
        driver.quit();
    }

    @Test
    public void testBase()
    {
        driver.get("https://www.google.com");
    }
}
