package Models;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class Configuration {
    @JsonProperty("tag")
    public String tag;

    @JsonProperty("type")
    public String type;

    @JsonProperty("baseUrl")
    public String baseURL;

    @JsonProperty("selenoidUri")
    public String selenoidUri;

    @JsonProperty("webDriverType")
    public String webDriverType;

    @JsonProperty("deleteEvidencesForPassedTests")
    public Boolean deleteEvidencesForPassedTests;

    @JsonProperty("capabilities")
    public Collection<Capability> capabilities;
}
