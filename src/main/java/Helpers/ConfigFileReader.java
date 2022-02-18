package Helpers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public final class ConfigFileReader {

    private static Properties properties;
    private static String propertyFilePath= "src//configs//Configuration.properties";

    public static void readConfiguration() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(propertyFilePath));
            properties = new Properties();
            try {
                properties.load(reader);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Configuration.properties not found at " + propertyFilePath);
        }
    }

    public static String getDriverPath() {
        String driverPath = properties.getProperty("driverPath");
        if(driverPath!= null) return driverPath;
        else throw new RuntimeException("driverPath not specified in the Configuration.properties file.");
    }

    public static String getEvidencesFolder() {
        String evidencesFolder = properties.getProperty("evidencesFolder");
        if(evidencesFolder!= null) return evidencesFolder;
        else throw new RuntimeException("evidencesFolder not specified in the Configuration.properties file.");
    }
}
