package PageObjectModel;

import Helpers.RetryPolicies;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PageBase {

    private static final int TIMEOUT = 5;
    private static final String ELEMENT_TO_CHECK_IS_NULL_MESSAGE = "Element to check was null.";

    protected WebDriver driver;
    private WebDriverWait wait;

    public PageBase(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT));
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

    protected void checkElementExists(List<WebElement> elementList) {
        checkElementExists(elementList, "Expected element to exist.");
    }

    protected void checkElementExists(List<WebElement> elementList, String failMessage) {
        checkElementExists(elementList, failMessage, TIMEOUT);
    }

    protected void checkElementExists(List<WebElement> elementList, int numberOfRetries) {
        checkElementExists(elementList, "Expected element to exist.", numberOfRetries);
    }

    protected void checkElementExists(List<WebElement> elementList, String failMessage, int numberOfRetries) {
        if (elementList != null) {
            RetryPolicies.executeActionWithRetries(() -> assertThat(!elementList.isEmpty()).withFailMessage(failMessage).isTrue(), numberOfRetries);
        } else {
            throw new NullPointerException(ELEMENT_TO_CHECK_IS_NULL_MESSAGE);
        }
    }
}