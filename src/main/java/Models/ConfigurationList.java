package Models;

import io.cucumber.core.internal.com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public class ConfigurationList {
    @JsonProperty("configurations")
    public Collection<Configuration> configurations;

    @JsonProperty("evidencesFolder")
    public String evidencesFolder;

    @JsonProperty("downloadDataPath")
    public String downloadDataPath;

    @JsonProperty("postgresqlConnectionString")
    public String postgresqlConnectionString;

    @JsonProperty("postgresqlUser")
    public String postgresqlUser;

    @JsonProperty("postgresqlPassword")
    public String postgresqlPassword;

    @JsonProperty("mongoDbConnectionString")
    public String mongoDbConnectionString;
}
