package PageObjectModel;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends PageBase {

    private static final String LOGIN_URL = "https://www.saucedemo.com/";

    @FindBy(name="user-name")
    WebElement userIdTextBox;

    @FindBy(name="password")
    WebElement userPasswordTextBox;

    @FindBy(name="login-button")
    WebElement loginButton;

    public HomePage(WebDriver driver) {
        super(driver);
        driver.get(LOGIN_URL);
    }

    public void clickLoginButton(){
        clickElement(this.loginButton);
    }

    public void enterUserName(String text){
        sendKeysElement(userIdTextBox, text);
    }

    public void enterUserPassword(String text){
        sendKeysElement(userPasswordTextBox, text);
    }

    public void checkUserIsAtHomePage() {
        Assert.assertEquals("Different URL", driver.getCurrentUrl(), LOGIN_URL);
    }
}
