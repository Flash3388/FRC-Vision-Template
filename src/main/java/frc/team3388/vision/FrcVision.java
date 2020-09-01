package frc.team3388.vision;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.processing.color.HsvColorSettings;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import frc.team3388.vision.config.Config;

import java.util.List;

public class FrcVision {

    private final Config mConfig;
    private final CameraControl mCameraControl;
    private final NtControl mNtControl;
    private final ThrowableHandler mThrowableHandler;

    public FrcVision(Config config, CameraControl cameraControl, NtControl ntControl, ThrowableHandler throwableHandler) {
        mConfig = config;
        mCameraControl = cameraControl;
        mNtControl = ntControl;
        mThrowableHandler = throwableHandler;
    }

    public void startVision() {
        mNtControl.startNetworkTables();
        List<VideoSource> cameras = mCameraControl.startCameras();
        mNtControl.initializeCameraSwitching(cameras);

        if (cameras.size() >= 1) {
            startVisionThread(cameras, mConfig);
            waitForever();
        } else {
            System.out.println("No cameras");
        }
    }

    private void startVisionThread(List<VideoSource> cameras, Config config) {
        VideoSource camera = cameras.get(0);
        mNtControl.initializeExposureControl(camera);

        CvSource cvSource = CameraServer.getInstance()
                .putVideo("processed", 480, 320);

        HsvColorSettings colorSettings = mNtControl.colorSettings();

        new VisionControl(
                new CvProcessing(),
                cvSource, colorSettings, mNtControl, config.getTargetConfig(), mThrowableHandler)
                .startForCamera(camera, config.getCameraConfigs().get(0));
    }

    private static void waitForever() {
        for (; ; ) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }
}
