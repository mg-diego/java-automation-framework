import org.junit.jupiter.api.Test;

public class Login extends BaseTest {

    @Test
    public void Login()
    {
        HomePage homePage = new HomePage(getDriver());
        InventoryPage inventoryPage = new InventoryPage(getDriver());

        homePage.enterUserName("standard_user");
        homePage.enterUserPassword("secret_sauce");
        homePage.clickLoginButton();
        inventoryPage.checkUserIsAtInventory();
    }

    @Test
    public void Logout()
    {
        HomePage homePage = new HomePage(getDriver());
        InventoryPage inventoryPage = new InventoryPage(getDriver());
        MenuPage menuPage = new MenuPage(getDriver());

        homePage.enterUserName("standard_user");
        homePage.enterUserPassword("secret_sauce");
        homePage.clickLoginButton();
        inventoryPage.checkUserIsAtInventory();
        menuPage.openMenu();
        menuPage.logout();
        homePage.checkUserIsAtHomePage();
    }
}
