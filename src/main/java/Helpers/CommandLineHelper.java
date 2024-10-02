package Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class CommandLineHelper {

    public static void executeCommand(String command) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", command);
        builder.redirectErrorStream(true);
        builder.start();
    }

    public static boolean executeCommandWithTimeout(String command, int timeoutInSeconds) {
        try {
            Process process = Runtime.getRuntime().exec("cmd.exe /c " + command);
            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            StringBuilder errorMessage = new StringBuilder();
            String line;
            while ((line = errorReader.readLine()) != null) {
                errorMessage.append(line).append("\n");
            }
            boolean processCompleted = process.waitFor(timeoutInSeconds, TimeUnit.SECONDS);

            if (processCompleted) {
                if (process.exitValue() == 0) {
                    return true;
                } else {
                    System.err.println("Command failed with exit code " + process.exitValue());
                    System.err.println("Error Message:\n" + errorMessage);
                    return false;
                }
            } else {
                System.err.println("Command timed out.");
                process.destroy();
                return false;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
