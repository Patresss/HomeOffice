package com.patres.homeoffice.light;

import java.util.Arrays;
import java.util.Optional;

import static com.patres.homeoffice.light.LightManager.*;

public enum LightMode {

    AVAILABLE(lightManager -> lightManager.turnOn(GREEN)),
    WORKING(lightManager -> lightManager.turnOn(YELLOW)),
    MEETING(lightManager -> lightManager.turnOn(RED)),
    TURN_OFF(LightManager::turnOff),
    AUTOMATION(LightManager::detectAutomationChanges);

    private static final String BUTTON_SUFFIX = "Button";
    private static final String ENUM_SEPARATOR = "_";
    private static final String CSS_SEPARATOR = "-";
    private final LightConsumer lightConsumer;

    LightMode(LightConsumer lightConsumer) {
        this.lightConsumer = lightConsumer;
    }

    public String getName() {
        return name().toLowerCase().replace(ENUM_SEPARATOR, CSS_SEPARATOR);
    }

    public static Optional<LightMode> findByButtonsId(final String id) {
        final String modeName = id.replace(BUTTON_SUFFIX, "");
        return Arrays.stream(values())
                .filter(lightMode -> lightMode.getName().replace(CSS_SEPARATOR, "").equalsIgnoreCase(modeName))
                .findFirst();
    }

    public void handle(final LightManager lightManager) {
        lightConsumer.changeLight(lightManager);
    }
}
