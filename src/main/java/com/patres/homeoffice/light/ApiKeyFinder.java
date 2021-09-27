package com.patres.homeoffice.light;

import com.patres.homeoffice.settings.SettingsManager;
import io.github.zeroone3010.yahueapi.Hue;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

import static org.slf4j.LoggerFactory.getLogger;

public class ApiKeyFinder {

    private static final Logger logger = getLogger(ApiKeyFinder.class);

    private static final String PHILIPS_HUE_APPLICATION_NAME = "HomeOffice";
    private final SettingsManager settingsManager;
    private final Image icon;

    private boolean apiIsDetected = false;

    public ApiKeyFinder(final SettingsManager settingsManager, final Image icon) {
        this.settingsManager = settingsManager;
        this.icon = icon;
    }

    public void findKey() {
        final Alert alert = createAlert();
        final Thread thread = new Thread(() -> sendRequestToFindApiKey(alert));
        thread.start();
        alert.showAndWait();
        if (!apiIsDetected) {
            System.exit(0);
        }
    }

    private Alert createAlert() {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Push the Philips Hue button");
        alert.setHeaderText(null);
        alert.setContentText("Push the button on your Hue Bridge to resolve the apiKey future. You have 30 seconds");
        alert.getButtonTypes().setAll(new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE)); // without this button I cannot call alert.close()

        final Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(icon);
        return alert;
    }

    public void sendRequestToFindApiKey(final Alert alert) {
        try {
            logger.info("Sending a request to find API key");
            final String bridgeIp = settingsManager.getSettings().light().phlipsHueIp();
            logger.info("IP: {}", bridgeIp);

            final CompletableFuture<String> apiKeyProvider = Hue.hueBridgeConnectionBuilder(bridgeIp).initializeApiConnection(PHILIPS_HUE_APPLICATION_NAME);
            logger.info("Waiting for the api key...");
            final String apiKey = apiKeyProvider.get();
            logger.info("API key is found");
            settingsManager.saveApiKey(apiKey);
            apiIsDetected = true;
            Platform.runLater(alert::close);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            Platform.runLater(() -> new ErrorDialog(e).show());
        }
    }

}
