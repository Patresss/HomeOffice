package com.patres.homeoffice;

import com.patres.homeoffice.exception.ApplicationException;
import com.patres.homeoffice.light.ApiKeyFinder;
import com.patres.homeoffice.light.ErrorDialog;
import com.patres.homeoffice.light.LightManager;
import com.patres.homeoffice.settings.SettingsManager;
import com.patres.homeoffice.ui.PrimaryWindow;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ApplicationLauncher extends Application {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationLauncher.class);

    public static void main(final String[] args) {
        logger.info("Application is running");
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage){
        logger.info("UI is running");
        try {
            final SettingsManager settingsManager = createSettingsManager();
            final Image image = findIcon();

            if (!settingsManager.hasApiKey()) {
                findApiKey(image, settingsManager);
            }
            logger.info("Creating LightManager");
            final LightManager phlipsHueManager = new LightManager(settingsManager);
            logger.info("Creating PrimaryWindow");
            final PrimaryWindow primaryWindow = new PrimaryWindow(primaryStage, phlipsHueManager, settingsManager, image);
            logger.info("Creating window");
            final Stage window = primaryWindow.createWindow();
            logger.info("Showing window");
            window.show();
        } catch (Exception e) {
            new ErrorDialog(e).show();
        }
    }

    private Image findIcon() {
        logger.info("Finding icon");
        final Image icon = Optional.ofNullable(PrimaryWindow.class.getResourceAsStream("/icon/desktop/main-icon.png"))
                .map(Image::new)
                .orElseThrow(() -> new ApplicationException("Cannot find main icon"));
        logger.info("Icon is found");
        return icon;
    }

    private SettingsManager createSettingsManager() {
        logger.info("Setting are loading");

        final SettingsManager settingsManager = new SettingsManager("config/settings.yaml");
        settingsManager.calculateDefaultValues();
        settingsManager.validateSettings();

        logger.info("Setting are loaded");
        return settingsManager;
    }

    private void findApiKey(final Image image, final SettingsManager settingsManager) {
        final ApiKeyFinder apiKeyFinder = new ApiKeyFinder(settingsManager, image);
        apiKeyFinder.findKey();
    }

}