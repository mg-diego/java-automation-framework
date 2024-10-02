package Helpers;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EvidencesHelper {

    private final String id;
    private String testExecutionDetailsLogFilePath;


    public EvidencesHelper() {
        this.id = UUID.randomUUID().toString();
    }

    public static byte[] pngBytesToJpgBytes(byte[] pngBytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(pngBytes);
        BufferedImage bufferedImage = ImageIO.read(bais);

        BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                bufferedImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(newBufferedImage, "JPG", baos);

        return baos.toByteArray();
    }

    public void createTestExecutionDetailsLogFile(String scenarioName) {
        try {
            testExecutionDetailsLogFilePath = String.format("%s\\%s.txt", ConfigFileReader.getEvidencesFolder(), scenarioName);
            File myObj = new File(testExecutionDetailsLogFilePath);
            myObj.createNewFile();

            FileWriter myWriter = new FileWriter(testExecutionDetailsLogFilePath);
            myWriter.write("CHAPTER01=00:00:00.000");
            myWriter.write("\nCHAPTER01NAME=Step 1 - SETUP");
            myWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addStepToTestExecutionDetailsLogFile(Integer stepNumber, String stepName, long nanoseconds) throws IOException {
        long minutes = TimeUnit.NANOSECONDS.toMinutes(nanoseconds);
        long seconds = TimeUnit.NANOSECONDS.toSeconds(nanoseconds) - TimeUnit.MINUTES.toSeconds(minutes);
        long millis = TimeUnit.NANOSECONDS.toMillis(nanoseconds) - TimeUnit.SECONDS.toMillis(seconds);

        var textToAppend = String.format("\nCHAPTER%02d=00:%02d:%02d.%03d", stepNumber, minutes, seconds, millis);
        textToAppend += String.format("\nCHAPTER%02dNAME=Step %s - %s", stepNumber, stepNumber, stepName);
        Files.write(Paths.get(testExecutionDetailsLogFilePath), textToAppend.getBytes(), StandardOpenOption.APPEND);
    }
}
