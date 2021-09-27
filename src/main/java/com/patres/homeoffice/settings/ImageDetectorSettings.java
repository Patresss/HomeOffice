package com.patres.homeoffice.settings;

public record ImageDetectorSettings(
        Double imageDetectorThreshold,
        Integer windowsBarStartXPosition,
        Integer windowsBarStartYPosition,
        Integer windowsBarWidth,
        Integer windowsBarHight
) {

}
