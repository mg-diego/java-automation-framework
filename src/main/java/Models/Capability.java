package Models;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonProperty;

public class Capability {
    @JsonProperty("driverType")
    public String driverType;

    @JsonProperty("driverPath")
    public String driverPath;

    @JsonProperty("apiType")
    public String apiType;

    @JsonProperty("baseUrl")
    public String baseURL;
}
