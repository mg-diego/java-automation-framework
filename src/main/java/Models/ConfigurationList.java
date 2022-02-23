package Models;

import io.cucumber.messages.internal.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class ConfigurationList {
    @JsonProperty("configurations")
    public Collection<Configuration> configurations;

    @JsonProperty("evidencesFolder")
    public String evidencesFolder;
}
