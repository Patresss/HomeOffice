package com.patres.homeoffice.light;

@FunctionalInterface
public interface LightConsumer {

    void changeLight(LightManager lightManager);
}
