package Models;

import io.cucumber.messages.internal.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class Configuration {
    @JsonProperty("tag")
    public String tag;

    @JsonProperty("type")
    public String type;

    @JsonProperty("baseUrl")
    public String baseURL;

    @JsonProperty("capabilities")
    public Collection<Capability> capabilities;
}
