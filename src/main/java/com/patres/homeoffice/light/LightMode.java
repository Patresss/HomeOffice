package com.patres.homeoffice.light;

import java.util.Arrays;
import java.util.Optional;

public enum LightMode {

    AVAILABLE(lightManager -> lightManager.turnOn(ColorType.GREEN.getPhilipsHueColor())),
    WORKING(lightManager -> lightManager.turnOn(ColorType.YELLOW.getPhilipsHueColor())),
    MEETING_MICROPHONE(lightManager -> lightManager.turnOn(ColorType.RED.getPhilipsHueColor())),
    MEETING_WEBCAM(lightManager -> lightManager.turnOn(ColorType.PURPLE.getPhilipsHueColor())),
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
