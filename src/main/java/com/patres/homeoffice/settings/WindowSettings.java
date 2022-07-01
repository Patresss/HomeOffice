package com.patres.homeoffice.settings;

public record WindowSettings(
        Boolean pinned,
        Boolean enablePreviousPosition,
        Integer positionX,
        Integer positionY
) {

}
