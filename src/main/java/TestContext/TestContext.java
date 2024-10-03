package TestContext;

import ApiResources.ResourceBase;
import DriverManager.WebDriverManager;
import Enums.Language;
import Helpers.EvidencesHelper;
import lombok.Getter;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.UUID;

public class TestContext {

    public EvidencesHelper evidencesHelper;
    public WebDriverManager webDriverManager;

    public String lastResponseBody;
    public String lastRequestUrl;
    @Getter
    public Type lastRequestType;
    private Map.Entry<String, CloseableHttpResponse> lastResponse;
    public InputStream lastResponseInputStream;
    public int lastResponseStatusCode = -1;
    @Getter
    private String userBearerToken = "";
    @Getter
    private String userRefreshToken = "default";
    public String id = UUID.randomUUID().toString();
    @Getter
    private Language language = Language.ENGLISH;

    public void setWebDriverManager(WebDriverManager webDriverManager) {
        this.webDriverManager = webDriverManager;
    }

    public void setEvidencesHelper(EvidencesHelper evidencesHelper) {
        this.evidencesHelper = evidencesHelper;
    }

    public void resetLastResponse() {
        lastResponse = null;
    }

    public int getLastResponseStatusCode() {
        return lastResponseStatusCode;
    }

    public String getLastResponseBody() {
        return lastResponseBody;
    }

    public String getLastRequestUrl() {
        return lastRequestUrl;
    }

    public void setLastResponse(Map.Entry<String, CloseableHttpResponse> response) throws IOException {
        setLastResponse(response, true, null);
    }

    public void setLastResponse(Map.Entry<String, CloseableHttpResponse> response, boolean storeOnlyResponseBody) throws IOException {
        setLastResponse(response, storeOnlyResponseBody, null);
    }

    public void setLastResponse(Map.Entry<String, CloseableHttpResponse> response, Type type) throws IOException {
        setLastResponse(response, true, type);
    }

    public void setLastResponse(Map.Entry<String, CloseableHttpResponse> response, boolean storeOnlyResponseBody, Type type) throws IOException {
        lastResponse = response;
        lastResponseStatusCode = ResourceBase.getStatusCode(lastResponse.getValue());
        lastRequestUrl = lastResponse.getKey();
        if (storeOnlyResponseBody) {
            lastResponseBody = ResourceBase.getBody(lastResponse.getValue());
        } else {
            lastResponseInputStream = ResourceBase.getEntity(lastResponse.getValue()).getContent();
        }
        if (type != null) {
            lastRequestType = type;
        }
    }

    public void setUserBearerToken(String bearerTokenValue) {
        userBearerToken = bearerTokenValue;
    }

    public void resetUserBearerTokenValues() {
        userBearerToken = "";
        userRefreshToken = "";
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
