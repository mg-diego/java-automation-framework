import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

public class TestingClass extends BaseTest {

    @Test
    public void test1()
    {
        WebDriver driver = getDriver();
        driver.get("https://www.google.com");
        String driverUrl = driver.getCurrentUrl();
        Assert.assertEquals(String.format("Expected URL to be 'this' but found (%s)", driverUrl), "https://www.google.com", driverUrl);
    }

    @Test
    public void test2()
    {
        WebDriver driver = getDriver();
        driver.get("https://www.google.com");
        String driverUrl = driver.getCurrentUrl();
        Assert.assertEquals(String.format("Expected URL to be 'this' but found (%s)", driverUrl), "https://www.google.com/", driverUrl);
    }
}
