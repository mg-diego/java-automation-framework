package StepDefinitions.API;


import Helpers.JsonFileCustomHelper;
import TestContext.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.assertj.core.api.AssertionsForClassTypes;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiValidatorSteps {

    private TestContext testContext;

    private List<Integer> httpStatusCodeOkList = Arrays.asList(200, 201, 204);

    public ApiValidatorSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @Then("the response message is empty")
    public void theResponseMessageIsEmpty() {
        assertThat(testContext.getLastResponseBody()).isEqualTo("[]");
    }

    @Then("the response message contains {string}")
    public void theResponseMessageContainsValue(String valueToFind) {
        assertThat(testContext.getLastResponseBody()).contains(valueToFind);
    }

    @And("the response message does not contain {string}")
    public void theResponseDoesNotContain(String result) {
        theResponseMessageContainsValueNumberOfTimes("0", result);
    }


    @Then("the response message contains {string} times the {string} value")
    public void theResponseMessageContainsValueNumberOfTimes(String expectedAmount, String valueToFind) {
        int actualAmount = testContext.getLastResponseBody().split(valueToFind, -1).length - 1;
        assertThat(actualAmount)
                .withFailMessage(String.format("Expected '%s' to contain '%s' value '%s' times but found '%s'\n TestContextId: %s", testContext.getLastResponseBody(), valueToFind, expectedAmount, actualAmount, testContext.id))
                .isEqualTo(Integer.parseInt(expectedAmount));
    }

    @And("the response message status code is {string}")
    public void theSimplifiedResponseStatusCodeIs(String expectedStatusCode) {
        var statusCode = this.testContext.getLastResponseStatusCode();

        assertThat(statusCode)
                .withFailMessage(String.format(
                        "Expected statusCode '%s' but found '%s': \n -Request URL: %s \n -Response Body: \n%s \n -TestContextId: %s",
                        expectedStatusCode,
                        this.testContext.getLastResponseStatusCode(),
                        this.testContext.getLastRequestUrl(),
                        this.testContext.getLastResponseBody(),
                        testContext.id))
                .isEqualTo(Integer.parseInt(expectedStatusCode));
    }

    @And("the {string} field inside error response message is {string}")
    @And("the {string} field inside the response message is {string}")
    public void theCodeFieldInResponseIs(String keyName, String expectedValue) throws Exception {
        var lastResponseBody = this.testContext.getLastResponseBody();
        String keyValue;
        var verboseMessage = String.format(
                "\nRequest URL: '%s' \nEndpoint response: '%s' \n User token: '%s' \n TestContextId: '%s'",
                testContext.getLastRequestUrl(),
                testContext.getLastResponseBody(),
                testContext.getUserBearerToken(),
                testContext.id
        );
        if (expectedValue.equals("NULL")) {
            AssertionsForClassTypes.assertThat(lastResponseBody)
                    .withFailMessage(String.format("'%s' field inside error response message is not null. %s", keyName, verboseMessage))
                    .doesNotContainIgnoringCase(String.format("\"%s\"", keyName));
        } else {
            keyValue = JsonFileCustomHelper.findPropertyInsideJson(lastResponseBody, keyName);
            if (expectedValue.equalsIgnoreCase("")) {
                AssertionsForClassTypes.assertThat(keyValue)
                        .withFailMessage(String.format(
                                "'%s' field inside error response message expected to be equal to:\n %s  \nbut instead it was:\n %s %s",
                                keyName,
                                expectedValue,
                                keyValue,
                                verboseMessage))
                        .isEqualTo(expectedValue);
            } else {
                AssertionsForClassTypes.assertThat(keyValue)
                        .withFailMessage(String.format(
                                "'%s' field inside error response message expected to contain:\n %s  \nbut instead it was:\n %s %s",
                                keyName,
                                expectedValue,
                                keyValue,
                                verboseMessage))
                        .contains(expectedValue);
            }
        }
    }
}
