package DriverManager;

import Helpers.ConfigFileReader;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

public final class WebDriverManager {

    private static WebDriver driver;

    public static WebDriver getDriver() {
        return driver;
    }

    public static void createDriverSession(String driverType){
        System.setProperty("webdriver.chrome.driver", ConfigFileReader.getDriverPath());

        switch (driverType.toLowerCase(Locale.ROOT)) {
            case "chrome" -> driver = new ChromeDriver();
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
