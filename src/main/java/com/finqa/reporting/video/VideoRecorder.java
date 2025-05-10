package com.finqa.reporting.video;

import org.monte.media.Format;
import org.monte.media.FormatKeys;
import org.monte.media.Registry;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.WebDriver;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.monte.media.FormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

public class VideoRecorder {
    
    private static final String VIDEO_FOLDER = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "videos";
    private ScreenRecorder screenRecorder;
    private String testName;
    private boolean isRecording = false;
    
    public VideoRecorder(String testName) {
        this.testName = testName;
    }
    
    public void startRecording() {
        // Verificar si estamos en un entorno headless
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Running in headless mode, video recording disabled for test: " + testName);
            return;
        }
        
        File videoFolder = new File(VIDEO_FOLDER);
        if (!videoFolder.exists()) {
            boolean created = videoFolder.mkdirs();
            if (!created) {
                System.err.println("Failed to create video folder: " + VIDEO_FOLDER);
                return;
            }
        }
        
        try {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int width = screenSize.width;
            int height = screenSize.height;
            
            Rectangle captureSize = new Rectangle(0, 0, width, height);
            
            GraphicsConfiguration gc = GraphicsEnvironment
                    .getLocalGraphicsEnvironment()
                    .getDefaultScreenDevice()
                    .getDefaultConfiguration();
            
            screenRecorder = new SpecializedScreenRecorder(gc, captureSize,
                    new Format(MediaTypeKey, FormatKeys.MediaType.FILE, MimeTypeKey, MIME_AVI),
                    new Format(MediaTypeKey, FormatKeys.MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                            DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                            QualityKey, 1.0f,
                            KeyFrameIntervalKey, 15 * 60),
                    new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                    null, videoFolder, testName);
            
            screenRecorder.start();
            isRecording = true;
            System.out.println("Started video recording for test: " + testName);
            
        } catch (IOException e) {
            System.err.println("Error starting video recording: " + e.getMessage());
            e.printStackTrace();
        } catch (AWTException e) {
            System.err.println("AWT Exception during video recording: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error in video recording: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public File stopRecording() {
        File video = null;
        if (!isRecording) {
            return null;
        }
        
        try {
            if (screenRecorder != null) {
                screenRecorder.stop();
                isRecording = false;
                
                if (!screenRecorder.getCreatedMovieFiles().isEmpty()) {
                    video = screenRecorder.getCreatedMovieFiles().get(0);
                    System.out.println("Video recording saved at: " + video.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            System.err.println("Error stopping video recording: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error stopping video recording: " + e.getMessage());
            e.printStackTrace();
        }
        return video;
    }
    
    private static class SpecializedScreenRecorder extends ScreenRecorder {
        
        private String fileName;
        private File movieFolder;
        
        public SpecializedScreenRecorder(GraphicsConfiguration cfg, Rectangle captureArea, 
                                         Format fileFormat, Format screenFormat, Format mouseFormat,
                                         Format audioFormat, File movieFolder, String fileName) throws IOException, AWTException {
            super(cfg, captureArea, fileFormat, screenFormat, mouseFormat, audioFormat, movieFolder);
            this.fileName = fileName;
            this.movieFolder = movieFolder;
        }
        
        @Override
        protected File createMovieFile(Format fileFormat) throws IOException {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timestamp = dateFormat.format(new Date());
            String safeFileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
            safeFileName = safeFileName + "_" + timestamp;
            
            return new File(movieFolder, safeFileName + "." + Registry.getInstance().getExtension(fileFormat));
        }
    }
}