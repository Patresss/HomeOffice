package com.patres.homeoffice.ui;

import com.jfoenix.controls.*;
import com.patres.homeoffice.ApplicationLauncher;
import com.patres.homeoffice.exception.ApplicationException;
import com.patres.homeoffice.light.LightManager;
import com.patres.homeoffice.settings.SettingsManager;
import com.patres.homeoffice.settings.WindowSettings;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

public class PrimaryWindow {

    private static final Logger logger = LoggerFactory.getLogger(PrimaryWindow.class);
    private final Stage primaryStage;
    private final LightManager lightManager;
    private final SettingsManager settingsManager;
    private final Image icon;

    public PrimaryWindow(final Stage primaryStage, final LightManager lightManager, final SettingsManager settingsManager, final Image icon) {
        this.primaryStage = primaryStage;
        this.lightManager = lightManager;
        this.settingsManager = settingsManager;
        this.icon = icon;
    }

    public Stage createWindow() throws IOException {
        final WindowSettings windowSettings = settingsManager.getSettings().window();
        final MainPane root = new MainPane(primaryStage, windowSettings.pinned(), lightManager);
        primaryStage.getIcons().add(icon);
        if (windowSettings.enablePreviousPosition()) {
            primaryStage.setX(windowSettings.positionX());
            primaryStage.setY(windowSettings.positionY());
        }
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Home office");
        primaryStage.setScene(createScene(root));

        makeDraggable(primaryStage, root);

        if (!SystemTray.isSupported()) {
            logger.error("SystemTray is not supported");
        } else {
            createTrayIcon();
        }
        return primaryStage;
    }

    private void createTrayIcon() {
        try {
            Platform.setImplicitExit(false);

            final PopupMenu popup = new PopupMenu();
            final URL url = Optional.ofNullable(ApplicationLauncher.class.getResource("/icon/desktop/small-icon.png"))
                    .orElseThrow(() -> new ApplicationException("Cannot find small icon"));
            final BufferedImage image = ImageIO.read(url);

            final TrayIcon trayIcon = new TrayIcon(image);
            trayIcon.addActionListener(item -> Platform.runLater(this::showStage));

            final SystemTray tray = SystemTray.getSystemTray();
            final MenuItem openItem = new MenuItem("Open");
            final MenuItem exitItem = new MenuItem("Exit");

            openItem.addActionListener(item -> Platform.runLater(this::showStage));
            exitItem.addActionListener(item -> {
                Platform.exit();
                tray.remove(trayIcon);
                System.exit(0);
            });
            popup.add(openItem);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);
            tray.add(trayIcon);
        } catch (AWTException | IOException e) {
            logger.error("Error in start method - I/O Exception", e);
        }
    }

    public void showStage() {
        primaryStage.show();
        primaryStage.toFront();
    }

    private Scene createScene(final Pane mainPane) {
        final Scene scene = new Scene(mainPane, Color.TRANSPARENT);
        final String cssFilePath = Optional.ofNullable(PrimaryWindow.class.getResource("/css/style_day.css"))
                .map(URL::toExternalForm)
                .orElseThrow(() -> new ApplicationException("Cannot find css file"));
        scene.getStylesheets().add(cssFilePath);
        return scene;
    }

    private void makeDraggable(final Stage stage, final Node byNode) {
        final Point dragDelta = new Point();
        byNode.setOnMousePressed(mouseEvent -> {
                    dragDelta.x = (int) (stage.getX() - mouseEvent.getScreenX());
                    dragDelta.y = (int) (stage.getY() - mouseEvent.getScreenY());
                    byNode.setCursor(Cursor.MOVE);
                }
        );
        final SimpleBooleanProperty inDrag = new SimpleBooleanProperty(false);

        byNode.setOnMouseReleased(event -> {
            byNode.setCursor(Cursor.HAND);
            inDrag.set(false);
            settingsManager.updateWindowPosition((int) stage.getX(), (int) stage.getY());
        });

        byNode.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() + dragDelta.x);
            stage.setY(mouseEvent.getScreenY() + dragDelta.y);
            inDrag.set(true);
        });
        byNode.setOnMouseEntered(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                byNode.setCursor(Cursor.HAND);
            }
        });
        byNode.setOnMouseExited(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                byNode.setCursor(Cursor.DEFAULT);
            }
        });
    }
}
