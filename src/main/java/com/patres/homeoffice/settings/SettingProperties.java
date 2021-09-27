package com.patres.homeoffice.settings;

public record SettingProperties(
        LightSettings light,
        ImageDetectorSettings imageDetector,
        WindowSettings window,
        WorkSettings workingTime
) {
}
