package StepDefinitions;

import DriverManager.WebDriverManager;
import Enums.BrowserType;
import Enums.BrowserVersion;
import Helpers.ConfigFileReader;
import Enums.DriverType;
import Helpers.EvidencesHelper;
import Models.ScenarioConfiguration;
import TestContext.TestContext;
import io.cucumber.core.backend.TestCaseState;
import io.cucumber.java.*;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestCase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class Hooks {

    private String _scenarioEvidencesPath;
    private int _stepCounter;
    private TestContext testContext;
    private DriverType scenarioType;

    public Hooks(TestContext testContext) {
        this.testContext = testContext;
        this.testContext.webDriverManager = new WebDriverManager();
    }

    @BeforeAll
    public static void beforeTestRun() {
        // Global config to be executed once before whole execution
        ConfigFileReader.readConfiguration();
    }

    @Before
    public void beforeScenario(Scenario scenario) throws MalformedURLException {
        createTestEvidencesFolder(scenario.getName());
        Collection<String> scenarioTags = scenario.getSourceTagNames().stream().toList();
        _stepCounter = 0;

        /* Open database connections
        MongoDbClientPool.getMongoClient();
        PostgresqlClient.openConnection();
        */

        if (!scenarioTags.stream().map(String::toLowerCase).collect(Collectors.toList()).contains("@ignore")) {
            testContext.setEvidencesHelper(new EvidencesHelper());

            // Reset testContext values in case they were used in BeforeTestRun
            testContext.resetUserBearerTokenValues();
            testContext.resetLastResponse();

            // Clean database tables before test.

            for (String scenarioTag : scenarioTags) {
                var tag = scenarioTag.replace("@", "").toUpperCase(Locale.ROOT);

                if (tag.toUpperCase(Locale.ROOT).contains(DriverType.WEB.toString())) {
                    testContext.setWebDriverManager(new WebDriverManager());
                    testContext.webDriverManager.createDriverSession(generateScenarioConfiguration(scenario.getName()));
                    scenarioType = DriverType.WEB;
                    break;
                }
                if (tag.toUpperCase(Locale.ROOT).contains(DriverType.API.toString())) {
                    // No initial setup needed
                }
            }
        }
    }

    @After
    //This runs in decrements order, means apposite of @Before. Value 1 would run first and 0 would be after 1.
    public void afterScenario(Scenario scenario) throws NoSuchFieldException, IllegalAccessException {
        Collection<String> scenarioTags = new ArrayList<>(scenario.getSourceTagNames());

        if (!scenarioTags.stream().map(String::toLowerCase).collect(Collectors.toList()).contains("@ignore")) {
            if (testContext != null && testContext.webDriverManager != null) {
                var networkLogs = testContext.webDriverManager.getNetworkLogs();
                networkLogs.forEach(log -> scenario.attach(log, "text/plain", "Network log"));
                testContext.webDriverManager.closeDriverSession();
            }
        }

        if (checkIfScenarioIsPassed(scenario)) {
            if (ConfigFileReader.getDeleteEvidencesForPassedTests() && scenarioType != DriverType.API) {
                var attachment = new File(Paths.get(ConfigFileReader.getEvidencesFolder(), scenario.getName() + ".mp4").toString());
                FileUtils.waitFor(attachment, 10);
                FileUtils.deleteQuietly(attachment);
            }
        }
    }

    @AfterStep
    public void afterStep(Scenario scenario) throws IOException, NoSuchFieldException, IllegalAccessException {
        if (scenarioType == DriverType.WEB) {
            String screenshotPath = generateScreenshotPath(scenario);
            WebDriverManager.takeScreenshot(screenshotPath);
            scenario.attach(screenshotPath,"image/png", Paths.get(screenshotPath).getFileName().toString());
            _stepCounter++;
        }
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
        _scenarioEvidencesPath = String.format("%s\\%s - %s", ConfigFileReader.getEvidencesFolder(), scenarioName, date);
        new File(_scenarioEvidencesPath).mkdirs();
    }

    private ScenarioConfiguration generateScenarioConfiguration(String scenarioName) {
        try {
            var configurationString = scenarioName.substring(scenarioName.indexOf("[") + 1);
            configurationString = configurationString.substring(0, configurationString.indexOf("]"));
            List<String> configuration = Arrays.stream(configurationString.split("-")).collect(Collectors.toList());
            return ScenarioConfiguration.builder()
                    .browserType(BrowserType.valueOf(configuration.get(0).trim()))
                    .browserVersion(WebDriverManager.getBrowserVersion(
                            BrowserType.valueOf(configuration.get(0).trim()),
                            BrowserVersion.valueOf(configuration.get(1).trim().substring(1))))
                    .resolution(Arrays.stream(configuration.get(2).trim().split("]")).findFirst().get())
                    .testName(scenarioName)
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ScenarioConfiguration.builder()
                    .browserType(BrowserType.CHROME)
                    .browserVersion("MAXIMUM_VERSION")
                    .resolution("1920x1080")
                    .testName(scenarioName)
                    .build();
        }
    }

    private boolean checkIfScenarioIsPassed(Scenario scenario) throws NoSuchFieldException, IllegalAccessException {
        Field f = scenario.getClass().getDeclaredField("delegate");
        f.setAccessible(true);
        TestCaseState testcaseState = (TestCaseState) f.get(scenario);

        Field stepResultsField = testcaseState.getClass().getDeclaredField("stepResults");
        stepResultsField.setAccessible(true);
        List<Result> stepResults = (List<Result>) stepResultsField.get(testcaseState);

        return stepResults.stream().noneMatch(x -> x.getStatus() != Status.PASSED);
    }
}
