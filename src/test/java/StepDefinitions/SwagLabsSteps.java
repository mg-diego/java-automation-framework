package StepDefinitions;

import DriverManager.WebDriverManager;
import PageObjectModel.HomePage;
import PageObjectModel.InventoryPage;
import PageObjectModel.MenuPage;
import io.cucumber.java.en.*;

public class SwagLabsSteps {

    private HomePage homePage;
    private MenuPage menuPage;
    private InventoryPage inventoryPage;

    public SwagLabsSteps(){
        homePage = new HomePage(WebDriverManager.getDriver());
        menuPage = new MenuPage(WebDriverManager.getDriver());
        inventoryPage = new InventoryPage(WebDriverManager.getDriver());
    }

    @Given("the user enters the username {string}")
    public void theUserEntersTheUsernameUsername(String username) {
        homePage.enterUserName(username);
    }

    @And("the user enters password {string}")
    public void theUserEntersPasswordSecret_sauce(String password) {
        homePage.enterUserPassword(password);
    }

    @When("the user clicks on submit button")
    public void theUserClicksOnSubmitButton() {
        homePage.clickLoginButton();
    }

    @Then("the user can login")
    public void theUserCanLogin() {
        inventoryPage.checkUserIsAtInventory();
    }

    @When("the user opens menu")
    public void theUserOpensMenu() {
        menuPage.openMenu();
    }

    @And("the user clicks on Logout button")
    public void theUserClicksOnLogoutButton() {
        menuPage.logout();
    }

    @Then("the user is at homepage")
    public void theUserIsAtHomepage() {
        homePage.checkUserIsAtHomePage();
    }
}
