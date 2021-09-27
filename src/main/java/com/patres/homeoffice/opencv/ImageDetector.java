package com.patres.homeoffice.opencv;


import com.patres.homeoffice.exception.ApplicationException;
import com.patres.homeoffice.exception.UncheckedExceptionHandler;
import com.patres.homeoffice.settings.FileManager;
import com.patres.homeoffice.settings.ImageDetectorSettings;
import com.patres.homeoffice.settings.SettingsManager;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MultiResolutionImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class ImageDetector {

    private static final Logger logger = getLogger(ImageDetector.class);

    private static final String MIC_IMAGE_PATH = "config/mic.JPG";

    private final Robot robot = new Robot();
    private final byte[] templateByteArray;
    private final double thresholdMatch;
    private final Rectangle rectangleToSearch;

    public ImageDetector(final ImageDetectorSettings imageDetectorSettings) throws AWTException, IOException {
        this.rectangleToSearch = new Rectangle(imageDetectorSettings.windowsBarStartXPosition(), imageDetectorSettings.windowsBarStartYPosition(), imageDetectorSettings.windowsBarWidth(), imageDetectorSettings.windowsBarHight());
        this.thresholdMatch = imageDetectorSettings.imageDetectorThreshold();
        createMicImageIfDoesntExist();
        this.templateByteArray = Files.readAllBytes(Paths.get(MIC_IMAGE_PATH));
    }

    private void createMicImageIfDoesntExist() throws IOException {
        final FileManager fileManager = new FileManager(MIC_IMAGE_PATH);
        fileManager.createFileIfDoesntExist();
    }

    private byte[] calculateRectangleByte(final Rectangle rectangle) throws IOException {
        final DisplayMode defaultDisplayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        final MultiResolutionImage multiResolutionScreenCapture = robot.createMultiResolutionScreenCapture(rectangle);
        final BufferedImage capture = (BufferedImage) multiResolutionScreenCapture.getResolutionVariant(defaultDisplayMode.getWidth(), defaultDisplayMode.getHeight());
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(capture, "bmp", os);
        return os.toByteArray();
    }

    public boolean isImageDetected() {
        try {
            byte[] searchLocation = calculateRectangleByte(rectangleToSearch);
            final Mat image = Imgcodecs.imdecode(new MatOfByte(searchLocation), Imgcodecs.IMREAD_UNCHANGED);
            final Mat template = Imgcodecs.imdecode(new MatOfByte(templateByteArray), Imgcodecs.IMREAD_UNCHANGED);

            final int resultCols = image.cols() - template.cols() + 1;
            final int resultRows = image.rows() - template.rows() + 1;
            final Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1);

            Imgproc.matchTemplate(image, template, result, Imgproc.TM_CCOEFF_NORMED);

            final Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);
            logger.debug("Image detected score: " + minMaxLocResult.maxVal);
            return minMaxLocResult.maxVal >= thresholdMatch;
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

}