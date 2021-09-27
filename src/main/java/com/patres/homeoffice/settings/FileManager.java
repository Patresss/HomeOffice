package com.patres.homeoffice.settings;

import com.patres.homeoffice.exception.ApplicationException;
import com.patres.homeoffice.exception.UncheckedExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class FileManager {

    private final String filePath;

    public FileManager(String filePath) {
        this.filePath = filePath;
    }

    public void createFileIfDoesntExist() throws IOException {
        final Path destinationPath = Paths.get(filePath);
        if (!destinationPath.toFile().exists()) {
            final File parent = destinationPath.getParent().toFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            final Path filePathToCopy = Optional.ofNullable(SettingsManager.class.getResource("/" + filePath))
                    .map(UncheckedExceptionHandler.handle(URL::toURI))
                    .map(Paths::get)
                    .orElseThrow(() -> new ApplicationException("Cannot find path: " + filePath));
            Files.copy(filePathToCopy, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
