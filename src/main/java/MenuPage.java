import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class MenuPage extends PageBase {

    @FindBy(id="react-burger-menu-btn")
    private WebElement openMenuButton;

    @FindBy(id="logout_sidebar_link")
    private WebElement logoutButton;

    public MenuPage(WebDriver driver) {
        super(driver);
    }

    public void openMenu() {
        clickElement(this.openMenuButton);
    }

    public void logout() {
        clickElement(this.logoutButton);
    }
}
