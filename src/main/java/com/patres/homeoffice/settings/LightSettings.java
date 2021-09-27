package com.patres.homeoffice.settings;

import com.patres.homeoffice.light.LightMode;

public record LightSettings(
        String phlipsHueIp,
        String phlipsHueApiKey,
        String phlipsHueRoomName,
        String phlipsHueLightName,
        Integer brightnes,
        LightMode lightMode,
        Integer automationFrequencySeconds
) {

}
