package StepDefinitions.Web;

import TestContext.TestContext;

public class WebStepBase {
    public TestContext testContext;

    public WebStepBase(TestContext testContext) {
        this.testContext = testContext;
    }
}
