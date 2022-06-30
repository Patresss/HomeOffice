package com.patres.homeoffice.light;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.patres.homeoffice.light.ColorType.*;
import static java.util.stream.Collectors.toList;

public enum LightMode {

    AVAILABLE(lightManager -> lightManager.turnOn(GREEN.getPhilipsHueColor()), 4, automationProcess -> true),
    WORKING(lightManager -> lightManager.turnOn(YELLOW.getPhilipsHueColor()), 3, AutomationProcess::isWorkingTime),
    MEETING_MICROPHONE(lightManager -> lightManager.turnOn(RED.getPhilipsHueColor()), 2, AutomationProcess::isMicrophoneWorking),
    MEETING_WEBCAM(lightManager -> lightManager.turnOn(PURPLE.getPhilipsHueColor()), 1, AutomationProcess::isWebcamWorking),
    TURN_OFF(LightManager::turnOff),
    AUTOMATION(LightManager::detectAutomationChanges);

    public static final List<LightMode> AUTOMATION_MODES = Arrays.stream(values())
            .filter(mode -> mode.getAutomationOrder() > 0)
            .sorted(Comparator.comparingInt(LightMode::getAutomationOrder))
            .collect(toList());
    private static final String BUTTON_SUFFIX = "Button";
    private static final String ENUM_SEPARATOR = "_";
    private static final String CSS_SEPARATOR = "-";
    private final LightConsumer lightConsumer;
    private final int automationOrder;
    private final Predicate<AutomationProcess> automationProcessPredicate;


    LightMode(LightConsumer lightConsumer, int automationOrder, Predicate<AutomationProcess> automationProcessPredicate) {
        this.lightConsumer = lightConsumer;
        this.automationOrder = automationOrder;
        this.automationProcessPredicate = automationProcessPredicate;
    }

    LightMode(LightConsumer lightConsumer) {
        this(lightConsumer, -1, null);
    }

    public String getName() {
        return name().toLowerCase().replace(ENUM_SEPARATOR, CSS_SEPARATOR);
    }

    public int getAutomationOrder() {
        return automationOrder;
    }

    public boolean isAutomationProcessRunning(final AutomationProcess automationProcess) {
        if (automationProcessPredicate == null) {
            return false;
        }
        return automationProcessPredicate.test(automationProcess);
    }

    public static Optional<LightMode> findByButtonsId(final String id) {
        final String modeName = id.replace(BUTTON_SUFFIX, "");
        return Arrays.stream(values())
                .filter(lightMode -> lightMode.getName().replace(CSS_SEPARATOR, "").equalsIgnoreCase(modeName))
                .findFirst();
    }

    public static LightMode getAutomationAction(final AutomationProcess automationProcess) {
        return AUTOMATION_MODES.stream()
                .filter(mode -> mode.isAutomationProcessRunning(automationProcess))
                .findFirst()
                .orElse(AVAILABLE);
    }


    public void handle(final LightManager lightManager) {
        lightConsumer.changeLight(lightManager);
    }
}
