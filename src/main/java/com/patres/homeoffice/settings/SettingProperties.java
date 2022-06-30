package com.patres.homeoffice.settings;

public record SettingProperties(
        LightSettings light,
        WindowSettings window,
        WorkSettings workingTime
) {
}
