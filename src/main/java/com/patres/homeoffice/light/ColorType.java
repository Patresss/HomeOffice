package com.patres.homeoffice.light;

import io.github.zeroone3010.yahueapi.Color;

public enum ColorType {

    GREEN(50, 205, 50),
    YELLOW(255, 213, 0),
    RED(177, 0, 0),
    PURPLE(128, 0, 128);

    private final Color philipsHueColor;

    ColorType(final int red, final int green, final int blue) {
        this.philipsHueColor = Color.of(red, green, blue);
    }

    public Color getPhilipsHueColor() {
        return philipsHueColor;
    }
}