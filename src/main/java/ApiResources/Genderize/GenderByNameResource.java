package ApiResources.Genderize;

import ApiResources.ResourceBase;
import Enums.ApiType;
import Helpers.ConfigFileReader;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.util.Map;

public class GenderByNameResource extends ResourceBase {
    private static final String RESOURCE_NAME = "?name=%s";
    private static final String BASE_URL = ConfigFileReader.getApiBaseUrl(ApiType.GENDERIZE);

    public static Map.Entry<String, CloseableHttpResponse> get(String name) throws IOException {
        return executeGetRequest(createEndpoint(BASE_URL + String.format(RESOURCE_NAME, name)), null);
    }
}
