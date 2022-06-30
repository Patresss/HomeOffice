package com.patres.homeoffice.registry;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.util.Arrays;

import static com.patres.homeoffice.registry.RegistryType.MICROPHONE;
import static com.patres.homeoffice.registry.RegistryType.WEBCAM;

public class RegistryManager {

    private static final String LAST_USED_TIME_STOP = "LastUsedTimeStop";
    private static final Long LAST_USED_TIME_STOP_VALUE = 0L;

    private RegistryManager() {
    }

    public static boolean isMicrophoneWorking() {
        return isDeviceWorking(MICROPHONE);
    }

    public static boolean isWebcamWorking() {
        return isDeviceWorking(WEBCAM);
    }
    private static boolean isDeviceWorking(final RegistryType registryType) {
        final String[] folders = Advapi32Util.registryGetKeys(WinReg.HKEY_CURRENT_USER, registryType.getNonPackagePath());
        return Arrays.stream(folders)
                .map(folder -> registryType.getNonPackagePath() + "\\" + folder)
                .map(register -> Advapi32Util.registryGetLongValue(WinReg.HKEY_CURRENT_USER, register, LAST_USED_TIME_STOP))
                .anyMatch(lastUsedTimeStop -> LAST_USED_TIME_STOP_VALUE.compareTo(lastUsedTimeStop) >= 0);
    }

}