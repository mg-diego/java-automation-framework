import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;

public class TestingClass extends BaseTest {

    @Test
    public void testBase()
    {
        WebDriver driver = getDriver();
        driver.get("https://www.google.com");
    }
}
