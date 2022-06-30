package com.patres.homeoffice.registry;

public enum RegistryType {

    MICROPHONE("microphone"),
    WEBCAM("webcam");

    private final String pathValue;

    RegistryType(String pathValue) {
        this.pathValue = pathValue;
    }

    public String getNonPackagePath() {
        return "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\CapabilityAccessManager\\ConsentStore\\" + pathValue + "\\NonPackaged";
    }

}