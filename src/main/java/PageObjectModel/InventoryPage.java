package PageObjectModel;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;

public class InventoryPage extends PageBase {

    private static final String INVENTORY_URL = "https://www.saucedemo.com/inventory.html";

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public void checkUserIsAtInventory() {
        Assert.assertEquals("Different URL", driver.getCurrentUrl(), INVENTORY_URL);
    }
}
