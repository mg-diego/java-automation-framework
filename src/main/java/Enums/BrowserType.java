package Enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BrowserType {
    CHROME("chrome"),
    FIREFOX("firefox"),
    EDGE("MicrosoftEdge");


    private final String name;
}
