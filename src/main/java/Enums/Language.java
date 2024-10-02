package Enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@AllArgsConstructor
@Getter
public enum Language {

    ENGLISH("en-US"),
    FRENCH_FR("fr-FR"),
    FRENCH_CA("fr-CA"),
    GERMAN("de-DE"),
    ITALIAN("it-IT"),
    PORTUGUESE("pt-PT"),
    SPANISH("es-ES"),
    DANISH("da-DK"),
    FINNISH("fi-FI"),
    DUTCH("nl-NL"),
    SWEDISH("sv-SE"),
    TURKISH("tr-TR");

    private String locale;

    public static Language mapFromLocale(String locale) {
        return EnumSet.allOf(Language.class)
                .stream()
                .filter(language -> language.getLocale().equalsIgnoreCase(locale))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(String.format("Language %s not found", locale)));
    }
}
