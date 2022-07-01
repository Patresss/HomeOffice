package com.patres.homeoffice.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.patres.homeoffice.exception.ApplicationException;
import com.patres.homeoffice.light.LightMode;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class SettingsManager {

    public static final String SETTING_PATH = "config/settings.yaml";
    private static final Logger logger = getLogger(SettingsManager.class);

    private final ObjectMapper mapper;
    private final String pathToFile;

    private SettingProperties settingProperties;

    public SettingsManager(final String pathToFile) {
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.mapper.registerModule(new JavaTimeModule());
        this.pathToFile = pathToFile;
        try {
            this.settingProperties = loadSettings();
        } catch (Exception e) {
            throw new ApplicationException("Unable to load settings. Make sure you run the program with administrative privileges.", e);
        }
    }

    public SettingProperties getSettings() {
        return settingProperties;
    }

    public void updateWindowPosition(final Integer positionX, final Integer positionY) {
        final WindowSettings windowSettings = new WindowSettings(settingProperties.window().pinned(), settingProperties.window().enablePreviousPosition(), positionX, positionY);
        final SettingProperties newSettingProperties = new SettingProperties(settingProperties.light(), windowSettings, settingProperties.workingTime());
        saveSettings(newSettingProperties);
    }

    public void updateLightMode(final LightMode lightMode) {
        final LightSettings light = new LightSettings(
                settingProperties.light().phlipsHueIp(),
                settingProperties.light().phlipsHueApiKey(),
                settingProperties.light().phlipsHueRoomName(),
                settingProperties.light().phlipsHueLightName(),
                settingProperties.light().brightnes(),
                lightMode,
                settingProperties.light().automationFrequencySeconds());
        final SettingProperties newSettingProperties = new SettingProperties(light, settingProperties.window(), settingProperties.workingTime());
        saveSettings(newSettingProperties);
    }


    public void saveApiKey(final String apiKey) {
        final LightSettings light = new LightSettings(
                settingProperties.light().phlipsHueIp(),
                apiKey,
                settingProperties.light().phlipsHueRoomName(),
                settingProperties.light().phlipsHueLightName(),
                settingProperties.light().brightnes(),
                settingProperties.light().lightMode(),
                settingProperties.light().automationFrequencySeconds());
        final SettingProperties newSettingProperties = new SettingProperties(
                light,
                settingProperties.window(),
                settingProperties.workingTime());
        saveSettings(newSettingProperties);
        logger.info("New api key is saved");
    }

    public boolean hasApiKey() {
        return settingProperties.light().phlipsHueApiKey() != null && !settingProperties.light().phlipsHueApiKey().isBlank();
    }

    private void saveSettings(final SettingProperties newSettingProperties) {
        try {
            final byte[] settingsAsByte = mapper.writeValueAsBytes(newSettingProperties);
            final Path path = Paths.get(pathToFile);
            Files.write(path, settingsAsByte);
            settingProperties = newSettingProperties;
        } catch (Exception e) {
            throw new ApplicationException("Unable to save settings. Make sure you run the program with administrative privileges.", e);
        }
    }

    private SettingProperties loadSettings() throws IOException {
        createSettingIfDoesntExist();
        final URL resource = Paths.get(pathToFile).toUri().toURL();
        return mapper.readValue(resource, SettingProperties.class);
    }

    private void createSettingIfDoesntExist() throws IOException {
        final FileManager fileManager = new FileManager(pathToFile);
        fileManager.createFileIfDoesntExist();
    }

    public void validateSettings() {
        final Map<Predicate<SettingProperties>, String> validationFields = Map.of(
                settings -> settings.light().phlipsHueIp() == null, "Philips hue ip cannot be empty",
                settings -> settings.light().phlipsHueRoomName() == null, "Philips room name cannot be empty"
        );
        final List<String> errors = validationFields.entrySet().stream()
                .filter(entry -> entry.getKey().test(settingProperties))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            throw new ApplicationException("Invalid setting file - " + SETTING_PATH + ": " + String.join(" ,", errors));
        }

    }

    public void calculateDefaultValues() {
        final LightSettings light = new LightSettings(
                settingProperties.light().phlipsHueIp(),
                settingProperties.light().phlipsHueApiKey(),
                settingProperties.light().phlipsHueRoomName(),
                settingProperties.light().phlipsHueLightName(),
                Optional.ofNullable(settingProperties.light().brightnes()).orElse(80),
                Optional.ofNullable(settingProperties.light().lightMode()).orElse(LightMode.AVAILABLE),
                Optional.ofNullable(settingProperties.light().automationFrequencySeconds()).orElse(1)
        );
        final WindowSettings windowSettings = new WindowSettings(
                Optional.ofNullable(settingProperties.window().pinned()).orElse(true),
                Optional.ofNullable(settingProperties.window().enablePreviousPosition()).orElse(false),
                Optional.ofNullable(settingProperties.window().positionX()).orElse(200),
                Optional.ofNullable(settingProperties.window().positionY()).orElse(200)
        );

        final WorkSettings work = new WorkSettings(
                settingProperties.workingTime().days(),
                Optional.ofNullable(settingProperties.workingTime().start()).orElse(LocalTime.of(9, 0)),
                Optional.ofNullable(settingProperties.workingTime().end()).orElse(LocalTime.of(17, 0))
        );
        final SettingProperties newSettingProperties = new SettingProperties(
                light,
                windowSettings,
                work);
        saveSettings(newSettingProperties);
    }

}
