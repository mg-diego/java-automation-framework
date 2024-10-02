package Helpers;

import Enums.BrowserType;
import Models.Configuration;
import Models.ConfigurationList;
import Enums.DriverType;
import Enums.WebDriverType;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public final class ConfigFileReader {

    private static ConfigurationList configurationList;
    private static String CONFIGURATION_FILE_PATH = "src//configurations//Configuration.json";
    private static final String SELENOID_BROWSERS_FILE_PATH = "src/external-resources/selenoid/browsers.json";

    public static void readConfiguration() {
        try {
            //Read JSON file
            String text = Files.readString(Paths.get(CONFIGURATION_FILE_PATH));
            configurationList = Converter.fromJsonString(text);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getEvidencesFolder() {
        return Optional.ofNullable(configurationList.evidencesFolder).orElseThrow(
                () -> new NullPointerException("evidencesFolder not specified in the Configuration.json file."));
    }

    public static String getDownloadDataPath() {
        return Optional.ofNullable(configurationList.downloadDataPath).orElseThrow(
                () -> new NullPointerException(("downloadDataPath not specified in the Configuration.json file.")));
    }

    public static WebDriverType getWebDriverType() {
        WebDriverType webDriverType = WebDriverType.valueOf(getWebDriverConfiguration().webDriverType.toUpperCase(Locale.ROOT));
        return Optional.of(webDriverType).orElseThrow(
                () -> new NullPointerException("webDriverType not specified in the Configuration.json file."));
    }

    public static String getWebDriverBaseUrl() {
        String baseUrl = getWebDriverConfiguration().baseURL;
        return Optional.ofNullable(baseUrl).orElseThrow(
                () -> new NullPointerException("baseUrl not specified in the Configuration.json file for WebDriver."));
    }

    public static String getSelenoidUri() {
        String selenoidUri = getWebDriverConfiguration().selenoidUri;
        return Optional.ofNullable(selenoidUri).orElseThrow(
                () -> new NullPointerException("selenoidUri not specified in the Configuration.json file."));
    }

    public static String getPostgresqlConnectionString() {
        String postgresqlConnectionString = configurationList.postgresqlConnectionString;
        return Optional.ofNullable(postgresqlConnectionString).orElseThrow(
                () -> new NullPointerException("postgresqlConnectionString not specified in the Configuration.json file."));
    }

    public static String getPostgresqlUser() {
        String databaseUser = configurationList.postgresqlUser;
        return Optional.ofNullable(databaseUser).orElseThrow(
                () -> new NullPointerException("postgresqlUser not specified in the Configuration.json file."));
    }

    public static String getPostgresqlPassword() {
        String databasePassword = configurationList.postgresqlPassword;
        return Optional.ofNullable(databasePassword).orElseThrow(
                () -> new NullPointerException("postgresqlPassword not specified in the Configuration.json file."));
    }

    public static String getMongoDbConnectionString() {
        String mongoDbConnectionString = configurationList.mongoDbConnectionString;
        return Optional.ofNullable(mongoDbConnectionString).orElseThrow(
                () -> new NullPointerException("mongoDbConnectionString not specified in the Configuration.json file."));
    }

    public static JSONObject getSelenoidBrowserVersions(BrowserType browser) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(SELENOID_BROWSERS_FILE_PATH);
        JSONTokener tokener = new JSONTokener(fis);
        var jsonObject = new JSONObject(tokener);
        JSONObject browserObject = jsonObject.getJSONObject(browser.getName());
        return browserObject.getJSONObject("versions");
    }

    public static Boolean getDeleteEvidencesForPassedTests() {
        Boolean selenoidUri = getWebDriverConfiguration().deleteEvidencesForPassedTests;
        return Optional.of(selenoidUri).orElseThrow(
                () -> new NullPointerException("deleteEvidencesForPassedTests not specified in the Configuration.json file."));
    }

    private static Configuration getWebDriverConfiguration() {
        return configurationList.configurations.stream()
                .filter(p -> Objects.equals(p.tag.toUpperCase(Locale.ROOT), DriverType.WEB.toString())).findFirst().get();
    }
}
