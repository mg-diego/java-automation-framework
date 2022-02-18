package StepDefinitions;

import DriverManager.WebDriverManager;
import Helpers.ConfigFileReader;
import io.cucumber.core.backend.TestCaseState;
import io.cucumber.java.*;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.TestCase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Hooks {

    private String _scenarioEvidencesPath;
    private int _stepCounter;

    @BeforeAll
    public static void beforeTestRun() {
        ConfigFileReader.readConfiguration();
    }

    @Before
    public void beforeScenario(Scenario scenario) {
        createTestEvidencesFolder(scenario.getName());
        Collection<String> scenarioTags = scenario.getSourceTagNames().stream().toList();
        _stepCounter = 0;

        for (String tag : scenarioTags) {
            WebDriverManager.createDriverSession(tag.replace("@", ""));
        }
    }

    @After
    public void afterScenario() {
        WebDriverManager.closeDriverSession();
    }

    @AfterStep
    public void afterStep(Scenario scenario) throws IOException, NoSuchFieldException, IllegalAccessException {

        String screenshotPath = generateScreenshotPath(scenario);
        WebDriverManager.takeScreenshot(screenshotPath);
        scenario.attach(screenshotPath,"image/png", Paths.get(screenshotPath).getFileName().toString());
        _stepCounter++;
    }

    private String generateScreenshotPath(Scenario scenario) throws NoSuchFieldException, IllegalAccessException {
        Field f = scenario.getClass().getDeclaredField("delegate");
        f.setAccessible(true);
        TestCaseState sc = (TestCaseState) f.get(scenario);

        Field f1 = sc.getClass().getDeclaredField("testCase");
        f1.setAccessible(true);
        TestCase testCase = (TestCase) f1.get(sc);

        List<PickleStepTestStep> testSteps = testCase.getTestSteps().stream().filter(x -> x instanceof PickleStepTestStep).map(x -> (PickleStepTestStep) x).collect(Collectors.toList());

        PickleStepTestStep step = testSteps.get(_stepCounter);
        String stepType = step.getStep().getKeyword().toUpperCase(Locale.ROOT);
        String stepText = step.getStep().getText();

        return _scenarioEvidencesPath + "\\" + _stepCounter + " - " + stepType + stepText;
    }

    private void createTestEvidencesFolder(String scenarioName) {
        String date = DateTimeFormatter.ofPattern("yyyy-MM-dd hh-mm-ss").withZone(ZoneId.of("UTC"))
                .format(Instant.now());
        Helpers.ConfigFileReader.getDriverPath();
        _scenarioEvidencesPath = String.format("%s\\%s - %s", ConfigFileReader.getEvidencesFolder(), scenarioName, date);
        new File(_scenarioEvidencesPath).mkdirs();
    }
}
