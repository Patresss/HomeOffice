package com.patres.homeoffice.light;

import com.patres.homeoffice.exception.ApplicationException;
import com.patres.homeoffice.settings.LightSettings;
import com.patres.homeoffice.settings.SettingsManager;
import io.github.zeroone3010.yahueapi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.patres.homeoffice.light.LightMode.AUTOMATION;

public class LightManager {

    private static final Logger logger = LoggerFactory.getLogger(LightManager.class);

    private final String roomName;
    private final Integer brightness;
    private final Integer automationFrequencySeconds;
    private final Room room;
    private final Light light;
    private final SettingsManager settingsManager;
    private final AutomationProcess automationProcess;

    private LightMode currentLightMode;
    private State currentState;

    public LightManager(final SettingsManager settingsManager) {
        final LightSettings lightSettings = settingsManager.getSettings().light();

        logger.info("Creating Hue...");
        logger.debug("phlipsHueIp: {}, phlipsHueApiKey: {} ", lightSettings.phlipsHueIp(), lightSettings.phlipsHueApiKey());
        final Hue hue = new Hue(lightSettings.phlipsHueIp(), lightSettings.phlipsHueApiKey());

        this.automationProcess = new AutomationProcess(this, settingsManager.getSettings().workingTime());
        this.roomName = lightSettings.phlipsHueRoomName();
        this.brightness = lightSettings.brightnes();
        logger.info("Finding room...");
        this.room = hue.getRoomByName(roomName)
                .orElseThrow(() -> new ApplicationException("Cannot find room by name: " + roomName));
        logger.info("Finding light...");
        this.light = this.room.getLightByName(lightSettings.phlipsHueLightName()).orElse(null);
        this.currentLightMode = lightSettings.lightMode();
        this.automationFrequencySeconds = lightSettings.automationFrequencySeconds();
        this.settingsManager = settingsManager;

        changeLightMode(currentLightMode);
    }

    public void changeLightMode(final LightMode lightMode) {
        if (currentLightMode != lightMode) {
            logger.info("Changing light mode: {} -> {}", currentLightMode, lightMode);
            currentLightMode = lightMode;
            lightMode.handle(this);
            settingsManager.updateLightMode(lightMode);
        }
    }

    public Optional<LightMode> getCurrentLightMode() {
        return Optional.ofNullable(currentLightMode);
    }

    void detectAutomationChanges() {
        final Thread thread = new Thread(() -> {
            while (currentLightMode == AUTOMATION) {
                automationProcess.turnOnAutomationProcess();
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
        if (!state.equals(currentState)) {
            logger.info("Turn on light: {}", state);
            currentState = state;
            if (light != null) {
                light.setState(state);
                light.setBrightness(brightness);
            } else {
                room.setState(state);
                room.setBrightness(brightness);
            }
        }
    }

    void turnOff() {
        logger.info("Turn off light");
        currentState = null;
        if (light != null) {
            light.turnOff();
        } else {
            room.turnOff();
        }
    }
}