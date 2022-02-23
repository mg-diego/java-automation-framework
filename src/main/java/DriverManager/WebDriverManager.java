package DriverManager;

import Helpers.ConfigFileReader;
import Models.BrowserType;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public final class WebDriverManager {

    private static WebDriver driver;

    public static WebDriver getDriver() {
        return driver;
    }

    public static void createDriverSession(){

        BrowserType browser = BrowserType.valueOf(ConfigFileReader.webDriverType.toUpperCase(Locale.ROOT));

        switch (browser) {
            case CHROME -> {
                System.setProperty("webdriver.chrome.driver", ConfigFileReader.getWebDriverPath());
                driver = new ChromeDriver();
            }
            case EDGE -> {
                System.setProperty("webdriver.gecko.driver", ConfigFileReader.getWebDriverPath());
                driver = new EdgeDriver();
            }
            case FIREFOX -> {
                System.setProperty("webdriver.edge.driver", ConfigFileReader.getWebDriverPath());
                driver = new FirefoxDriver();
            }
            default -> throw new UnsupportedOperationException();
        }

        driver.manage().window().maximize();
    }

    public static void closeDriverSession() {
        if(null != driver) {
            driver.close();
            driver.quit();
        }
    }

    public static void takeScreenshot(String screenshotPath) throws IOException {
        try{
            File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(screenshotPath + ".png"));
        }
        catch (Exception e) {

        }
    }
}
