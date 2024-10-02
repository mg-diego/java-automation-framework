package DriverManager;

import Enums.BrowserVersion;
import Helpers.ConfigFileReader;
import Helpers.RetryPolicies;
import Enums.BrowserType;
import Models.ScenarioConfiguration;
import Enums.WebDriverType;
import lombok.Getter;
import org.json.JSONObject;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Stream;

public final class WebDriverManager {

    private static final String BROWSER_NAME = "browserName";
    private static final String SELENOID_OPTIONS = "selenoid:options";
    @Getter
    private static WebDriver driver;
    @Getter
    private WebDriverType webDriverType;

    public void createDriverSession(ScenarioConfiguration scenarioConfiguration) throws MalformedURLException {
        webDriverType = ConfigFileReader.getWebDriverType();

        if (webDriverType == WebDriverType.LOCAL) {
            createLocalDriverInstance(scenarioConfiguration.browserType);
        } else if (webDriverType == WebDriverType.SELENOID) {
            createSelenoidDriverInstance(scenarioConfiguration);
        }

        driver.manage().window().maximize();
        driver.get(ConfigFileReader.getWebDriverBaseUrl());
    }

    public void closeDriverSession() {
        if (null != driver) {
            try {
                driver.quit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void takeScreenshot(String screenshotPath) {
        try{
            File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(screenshotPath + ".png"));
        }
        catch (Exception e) {

        }
    }

    public List<String> getNetworkLogs() {
        var networkLogs = new ArrayList<String>();
        try {
            LogEntries les = driver.manage().logs().get(LogType.PERFORMANCE);
            for (LogEntry le : les.getAll()) {
                JSONObject json = new JSONObject(le.getMessage());
                JSONObject message = json.getJSONObject("message");
                String method = message.getString("method");

                if (method != null) {
                    JSONObject params = message.getJSONObject("params");
                    if (params.has("response")) {
                        JSONObject jsonResponse = params.getJSONObject("response");
                        var log = "HTTP " + jsonResponse.getInt("status") + " - " + jsonResponse.getString("url");
                        networkLogs.add(log);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return networkLogs;
    }

    private void createLocalDriverInstance(BrowserType browser) {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", ConfigFileReader.getDownloadDataPath());
        prefs.put("download.prompt_for_download", Boolean.FALSE);
        prefs.put("profile.default_content_setting_values.automatic_downloads", 1);
        prefs.put("user_experience_metrics.personalization_data_consent_enabled", Boolean.TRUE);
        prefs.put("unexpectedAlertBehaviour", Boolean.TRUE);
        System.setProperty("webdriver.http.factory", "jdk-http-client");

        switch (browser) {
            case CHROME:
                ChromeOptions chromeOptions = getChromeOptions(prefs);
                chromeOptions.setCapability("unhandledPromptBehavior", "ignore");
                chromeOptions.addArguments("--disable-search-engine-choice-screen");
                driver = new ChromeDriver(chromeOptions);
                break;
            case EDGE:
                EdgeOptions edgeOptions = getEdgeOptions(prefs);
                edgeOptions.setCapability("unhandledPromptBehavior", "ignore");
                driver = new EdgeDriver(edgeOptions);
                break;
            case FIREFOX:
                FirefoxOptions firefoxOptions = getFirefoxOptions();
                firefoxOptions.setCapability("unhandledPromptBehavior", "ignore");
                driver = new FirefoxDriver(firefoxOptions);
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private void createSelenoidDriverInstance(ScenarioConfiguration scenarioConfiguration) throws MalformedURLException {
        ChromeOptions chromeOptions = null;
        EdgeOptions edgeOptions = null;
        FirefoxOptions firefoxOptions = null;

        Map<String, Object> selenoidOptions = new HashMap<>();
        selenoidOptions.put("browserVersion", scenarioConfiguration.browserVersion);
        selenoidOptions.put("screenResolution", scenarioConfiguration.resolution + "x24");
        selenoidOptions.put("name", scenarioConfiguration.testName);
        selenoidOptions.put("sessionTimeout", "3m");
        selenoidOptions.put("enableVNC", true);
        selenoidOptions.put("enableVideo", true);
        selenoidOptions.put("videoName", scenarioConfiguration.testName + ".mp4");
        selenoidOptions.put("enableLog", false);
        selenoidOptions.put("logName", scenarioConfiguration.testName + ".log");

        Map<String, Object> prefs = new LinkedHashMap<>();
        prefs.put("user_experience_metrics.personalization_data_consent_enabled", Boolean.TRUE);

        Map<String, Object> perfLogPrefs = new HashMap<>();
        perfLogPrefs.put("enableNetwork", true);
        perfLogPrefs.put("traceCategories", "devtools.network");

        switch (scenarioConfiguration.browserType) {
            case CHROME:
                chromeOptions = getChromeOptions(null);
                selenoidOptions.put(BROWSER_NAME, Browser.CHROME.browserName());
                chromeOptions.setExperimentalOption("perfLoggingPrefs", perfLogPrefs);
                chromeOptions.setCapability("unhandledPromptBehavior", "ignore");
                chromeOptions.addArguments("--disable-features=SidePanelPinning");
                break;
            case EDGE:
                edgeOptions = getEdgeOptions(null);
                selenoidOptions.put(BROWSER_NAME, Browser.EDGE.browserName());
                /*
                This argument is temporarily commented because it seems to affect the opening of new tabs in Edge (duplicateTab method)
                When set, it returns error message: ‘Failed to open new tab - no browser is open’.
                */
                // edgeOptions.addArguments("--guest");
                edgeOptions.setExperimentalOption("prefs", prefs);
                edgeOptions.setCapability("unhandledPromptBehavior", "ignore");
                break;
            case FIREFOX:
                firefoxOptions = getFirefoxOptions();
                selenoidOptions.put(BROWSER_NAME, Browser.FIREFOX.browserName());
                firefoxOptions.setCapability("unhandledPromptBehavior", "ignore");
                break;
            default:
                throw new UnsupportedOperationException();
        }

        var url = new URL(ConfigFileReader.getSelenoidUri());
        var browserOptions = Stream.of(chromeOptions, edgeOptions, firefoxOptions).filter(Objects::nonNull).findFirst().get();

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);

        browserOptions.setCapability("goog:loggingPrefs", logPrefs);
        browserOptions.setCapability(SELENOID_OPTIONS, selenoidOptions);
        browserOptions.setCapability("browserVersion", scenarioConfiguration.browserVersion);
        RetryPolicies.executeActionWithRetries(() ->
                driver = new RemoteWebDriver(url, browserOptions)
        );
    }

    public static String getBrowserVersion(BrowserType browser, BrowserVersion versionType) throws IOException {
        JSONObject versionsObject = ConfigFileReader.getSelenoidBrowserVersions(browser);

        String highestVersion = null;
        String lowestVersion = null;

        Iterator<String> keys = versionsObject.keys();
        while (keys.hasNext()) {
            String currentVersion = keys.next();
            if (highestVersion == null || compareMajorVersions(currentVersion, highestVersion) > 0) {
                highestVersion = currentVersion;
            }
            if (lowestVersion == null || compareMajorVersions(currentVersion, lowestVersion) < 0) {
                lowestVersion = currentVersion;
            }
        }

        switch (versionType) {
            case MAXIMUM_VERSION:
                return highestVersion;
            case MINIMUM_VERSION:
                return lowestVersion;
            default:
                throw new IllegalArgumentException("Invalid version type: " + versionType);
        }
    }

    private static int compareMajorVersions(String version1, String version2) {
        // Split the version strings by the dot
        String[] v1 = version1.split("\\.");
        String[] v2 = version2.split("\\.");

        // Parse the first component of each version string to obtain the major version of the driver
        int major1 = Integer.parseInt(v1[0]);
        int major2 = Integer.parseInt(v2[0]);

        // Compare the major versions
        return Integer.compare(major1, major2);
    }

    private ChromeOptions getChromeOptions(Map<String, Object> prefs) {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setAcceptInsecureCerts(true);
        chromeOptions.addArguments("--remote-allow-origins=*");
        if (prefs != null) {
            chromeOptions.setExperimentalOption("prefs", prefs);
        }
        return chromeOptions;
    }

    private EdgeOptions getEdgeOptions(Map<String, Object> prefs) {
        EdgeOptions edgeOptions = new EdgeOptions();
        edgeOptions.setAcceptInsecureCerts(true);
        if (prefs != null) {
            edgeOptions.setExperimentalOption("prefs", prefs);
        }
        return edgeOptions;
    }

    private FirefoxOptions getFirefoxOptions() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setAcceptInsecureCerts(true);
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.download.dir", ConfigFileReader.getDownloadDataPath());
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                "text/csv,application/java-archive,application/x-msexcel,application/excel,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/x-excel,application/vnd.ms-excel,image/png,image/jpeg,text/html,text/plain,application/msword,application/xml,application/vnd.microsoft.portable-executable,application/pdf");
        profile.setPreference("pdfjs.disabled", true);
        firefoxOptions.setProfile(profile);
        return firefoxOptions;
    }
}
