package PageObjectModel;

import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class InventoryPage extends PageBase {

    private static final String INVENTORY_URL = "https://www.saucedemo.com/inventory.html";

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public void checkUserIsAtInventory() {
        assertThat(driver.getCurrentUrl()).isEqualTo(INVENTORY_URL).withFailMessage("Expected same URL.");
    }
}
