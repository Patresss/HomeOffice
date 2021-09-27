module homeOffice {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;
    requires com.jfoenix;
    requires java.desktop;
    requires de.jensd.fx.glyphs.commons;
    requires java.logging;
    requires jdk.httpserver;
    requires de.jensd.fx.glyphs.fontawesome;
    requires opencv;
    requires slf4j.api;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.dataformat.yaml;
    requires yetanotherhueapi;
    requires com.fasterxml.jackson.datatype.jsr310;

    exports com.patres.homeoffice;
    exports com.patres.homeoffice.light to com.fasterxml.jackson.databind, javafx.fxml;
    exports com.patres.homeoffice.settings to com.fasterxml.jackson.databind;
    exports com.patres.homeoffice.ui to javafx.fxml;

    opens com.patres.homeoffice to javafx.fxml, javafx.graphics;
    opens com.patres.homeoffice.settings to com.fasterxml.jackson.databind;
    opens com.patres.homeoffice.ui to javafx.fxml;
    opens com.patres.homeoffice.light to com.fasterxml.jackson.databind, javafx.fxml;
}