package Models;

import Enums.BrowserType;
import lombok.Builder;

@Builder
public class ScenarioConfiguration {

    public BrowserType browserType;
    public String browserVersion;
    public String resolution;
    public String testName;
}
