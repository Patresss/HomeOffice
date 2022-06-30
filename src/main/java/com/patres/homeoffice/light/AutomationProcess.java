package com.patres.homeoffice.light;

import com.patres.homeoffice.settings.WorkSettings;

import java.time.LocalDateTime;

import static com.patres.homeoffice.registry.RegistryManager.isDeviceWorking;
import static com.patres.homeoffice.registry.RegistryType.MICROPHONE;
import static com.patres.homeoffice.registry.RegistryType.WEBCAM;

public class AutomationProcess {

    private final LightManager lightManager;
    private final WorkSettings workSettings;

    public AutomationProcess(final LightManager lightManager, final WorkSettings workSettings) {
        this.lightManager = lightManager;
        this.workSettings = workSettings;
    }

    public void turnOnAutomationProcess() {
        LightMode.getAutomationAction(this).handle(lightManager);
    }

    public boolean isWorkingTime() {
        final LocalDateTime now = LocalDateTime.now();
        return workSettings.days().contains(now.getDayOfWeek()) && now.toLocalTime().isAfter(workSettings.start()) && now.toLocalTime().isBefore(workSettings.end());
    }

    public boolean isMicrophoneWorking() {
        return isDeviceWorking(MICROPHONE);
    }

    public boolean isWebcamWorking() {
        return isDeviceWorking(WEBCAM);
    }
}
