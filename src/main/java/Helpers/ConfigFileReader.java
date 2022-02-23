package Helpers;

import Models.Configuration;
import Models.ConfigurationList;
import Models.DriverType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;

public final class ConfigFileReader {

    private static ConfigurationList configurationList;
    public static String webDriverType;
    private static String propertyFilePath= "src//configurations//Configuration.json";

    public static ConfigurationList getConfiguration() { return configurationList; }

    public static void readConfiguration() {

        try {
            //Read JSON file
            String text = new String(Files.readAllBytes(Paths.get(propertyFilePath)), StandardCharsets.UTF_8);
            configurationList = Converter.fromJsonString(text);
            webDriverType = configurationList.configurations.stream()
                    .filter(p -> Objects.equals(p.tag.toLowerCase(Locale.ROOT), DriverType.WEB.toString().toLowerCase(Locale.ROOT)))
                    .findFirst().get().type;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getWebDriverPath() {
        Configuration webDriverConfiguration = configurationList.configurations.stream()
                .filter(p -> Objects.equals(p.type, webDriverType)).findFirst().get();

        String driverPath = webDriverConfiguration.capabilities.stream().filter(x -> Objects.equals(x.driverType, webDriverType)).findFirst().get().driverPath;
        if(driverPath != null) return driverPath;
        else throw new RuntimeException(String.format("driverPath not specified for driverType '%s' in the Configuration.json file.", webDriverType));
    }

    public static String getEvidencesFolder() {
        if(configurationList.evidencesFolder != null) return configurationList.evidencesFolder;
        else throw new RuntimeException("evidencesFolder not specified in the Configuration.json file.");
    }
}
