package StepDefinitions.API;

import ApiResources.Genderize.GenderByNameResource;
import TestContext.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.io.IOException;

public class ApiGenderizeSteps {
    private TestContext testContext;

    public ApiGenderizeSteps(TestContext testContext) {
        this.testContext = testContext;
    }

    @When("the GET gender by name endpoint is requested with {string} name")
    public void theGETGenderByNameEndpointIsRequestedWithName(String name) throws IOException {
        this.testContext.setLastResponse(
                GenderByNameResource.get(name)
        );
    }
}

