package PageObjectModel;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PageBase {

    private static final int TIMEOUT = 5;
    private static final int POLLING = 100;

    protected WebDriver driver;
    private WebDriverWait wait;

    public PageBase(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, TIMEOUT);
    }

    protected void clickElement(WebElement element){
        if (element != null) {
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        }
        else {
            throw new NullPointerException("Element to be clicked was null.");
        }
    }

    protected void sendKeysElement(WebElement element, String text){
        if (element != null){
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.sendKeys(text);
        }
        else {
            throw new NullPointerException("Element to send keys was null.");
        }
    }

    protected void clearElementText(WebElement element){
        if (element != null){
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.clear();
        }
        else {
            throw new NullPointerException("Element to be cleared was null.");
        }
    }

    protected void waitForElementToAppear(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void waitForElementToDisappear(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected void waitForTextToDisappear(By locator, String text) {
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(locator, text)));
    }
}