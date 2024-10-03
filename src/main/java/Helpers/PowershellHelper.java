package Helpers;

import com.profesorfalken.jpowershell.PowerShell;

import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class PowershellHelper {

    private PowershellHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static String getHostName() throws IOException {
        return PowershellHelper.executeCommand("hostname");
    }

    public static void executeScriptFileWithParameterAs1String(String scriptPath, String parameter) throws ScriptException {
        var powerShell = PowerShell.openSession();
        var powerShellResponse = powerShell.executeScript(scriptPath, parameter);
        powerShell.close();
        if (powerShellResponse.isError() || powerShellResponse.isTimeout()) {
            throw new ScriptException(String.format("There was an error executing script: %n %s %s %n  FLAGS: isError: %s  - isTimeout: %s %n  - commandOutput: %s",
                    scriptPath,
                    parameter,
                    powerShellResponse.isError(),
                    powerShellResponse.isTimeout(),
                    powerShellResponse.getCommandOutput()));
        }
    }

    public static void executeScriptFileNoParameter(String scriptPath, int timeoutInSeconds) {
        executeScriptFileWithParameter(scriptPath, timeoutInSeconds);
    }

    public static void executeScriptFileWithParameter(String scriptPath, int timeoutInSeconds, String... parameters) {
        try {
            String[] command = new String[3 + parameters.length];
            command[0] = "powershell.exe";
            command[1] = "-File";
            command[2] = scriptPath;

            System.arraycopy(parameters, 0, command, 3, parameters.length);

            Process process = new ProcessBuilder(command).inheritIO().start();

            boolean processCompleted = process.waitFor(timeoutInSeconds, TimeUnit.SECONDS);
            if (!processCompleted) {
                System.out.println("process destroyed " + process.info());
                process.destroy();
            }

            if (!processCompleted) {
                throw new TimeoutException("PowerShell script execution timed out after " + timeoutInSeconds + " seconds.");
            }
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                System.out.println("PowerShell script executed with error code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public static String executeCommand(String powershellCommand) throws IOException {
        String command = "powershell.exe " + powershellCommand;
        Process powerShellProcess = Runtime.getRuntime().exec(command);
        powerShellProcess.getOutputStream().close();

        StringBuilder accumulatedOutput = new StringBuilder();
        String newLine = "";
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((newLine = stdout.readLine()) != null) {
            accumulatedOutput.append(newLine).append("\n");
        }
        return Arrays.stream(accumulatedOutput.toString().split("\n")).count() > 1
                ? accumulatedOutput.toString()
                : accumulatedOutput.toString().replace("\n", "");
    }
}
