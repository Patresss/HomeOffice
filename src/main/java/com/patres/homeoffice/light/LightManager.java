package com.patres.homeoffice.light;

import com.patres.homeoffice.exception.ApplicationException;
import com.patres.homeoffice.settings.LightSettings;
import com.patres.homeoffice.settings.SettingsManager;
import com.patres.homeoffice.settings.WorkSettings;
import io.github.zeroone3010.yahueapi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import static com.patres.homeoffice.light.LightMode.*;
import static com.patres.homeoffice.registry.RegistryManager.isMicrophoneWorking;

public class LightManager {

    static final Color GREEN = Color.of(50, 205, 50);
    static final Color YELLOW = Color.of(255, 213, 0);
    static final Color RED = Color.of(177, 0, 0);

    private static final Logger logger = LoggerFactory.getLogger(LightManager.class);

    private final String roomName;
    private final Integer brightness;
    private final Integer automationFrequencySeconds;
    private final Room room;
    private final Light light;
    private final WorkSettings workSettings;
    private final SettingsManager settingsManager;

    private LightMode currentLightMode;
    private LightMode currentLightState;

    public LightManager(final SettingsManager settingsManager) {
        final LightSettings lightSettings = settingsManager.getSettings().light();

        logger.info("Creating Hue...");
        logger.debug("phlipsHueIp: {}, phlipsHueApiKey: {} ", lightSettings.phlipsHueIp(), lightSettings.phlipsHueApiKey());
        final Hue hue = new Hue(lightSettings.phlipsHueIp(), lightSettings.phlipsHueApiKey());

        this.workSettings = settingsManager.getSettings().workingTime();
        this.roomName = lightSettings.phlipsHueRoomName();
        this.brightness = lightSettings.brightnes();
        logger.info("Finding room...");
        this.room = hue.getRoomByName(roomName).orElseThrow(() -> new ApplicationException("Cannot find room by name: " + roomName));
        logger.info("Finding light...");
        this.light = this.room.getLightByName(lightSettings.phlipsHueLightName()).orElse(null);
        this.currentLightMode = lightSettings.lightMode();
        this.automationFrequencySeconds = lightSettings.automationFrequencySeconds();
        this.settingsManager = settingsManager;

        changeLightMode(currentLightMode);
    }


    public void changeLightMode(final LightMode lightMode) {
        if (currentLightState != lightMode) {
            logger.info("Changing light mode: {} -> {}", currentLightMode, lightMode);
            currentLightState = lightMode;
            currentLightMode = lightMode;
            executeMode(lightMode);
            settingsManager.updateLightMode(lightMode);
        }
        currentLightMode = lightMode;
    }

    public void changeLightState(final LightMode lightMode) {
        if (currentLightState != lightMode) {
            logger.info("Changing light state: {} -> {}", currentLightState, lightMode);
            currentLightState = lightMode;
            executeMode(lightMode);
        }
    }

    void detectAutomationChanges() {
        final Thread thread = new Thread(() -> {
            while (currentLightMode == AUTOMATION) {
                turnOnAutomationProcess();
                try {
                    Thread.sleep(1000L * automationFrequencySeconds);
                } catch (InterruptedException e) {
                    throw new ApplicationException(e);
                }
            }
        });
        thread.start();
    }

    void turnOn(Color color) {
        final State state = State.builder().color(color).on();
        logger.info("Turn on light: {}", state);
        if (light != null) {
            light.setState(state);
            light.setBrightness(brightness);
        } else {
            room.setState(state);
            room.setBrightness(brightness);
        }
    }

    void turnOff() {
        logger.info("Turn off light");
        if (light != null) {
            light.turnOff();
        } else {
            room.turnOff();
        }
    }

    private void turnOnAutomationProcess() {
        if (isWorkingTime()) {
            if (isMicrophoneWorking()) {
                changeLightState(MEETING);
            } else {
                changeLightState(WORKING);
            }
        } else {
            changeLightState(AVAILABLE);
        }
    }

    private boolean isWorkingTime() {
        final LocalDateTime now = LocalDateTime.now();
        return workSettings.days().contains(now.getDayOfWeek()) && now.toLocalTime().isAfter(workSettings.start()) && now.toLocalTime().isBefore(workSettings.end());
    }

    private void executeMode(final LightMode lightMode) {
        lightMode.handle(this);
    }

}