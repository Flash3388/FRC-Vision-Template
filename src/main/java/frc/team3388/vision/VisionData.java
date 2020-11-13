package frc.team3388.vision;

import com.flash3388.flashlib.vision.control.VisionOption;
import com.flash3388.frc.nt.vision.NtVisionServer;
import frc.team3388.vision.config.CameraConfig;
import org.opencv.core.Mat;

public class VisionData {

    private final Mat mImage;
    private final CameraConfig mCameraConfig;
    private final NtVisionServer mVisionServer;

    public VisionData(Mat image, CameraConfig cameraConfig, NtVisionServer visionServer) {
        mImage = image;
        mCameraConfig = cameraConfig;
        mVisionServer = visionServer;
    }

    public Mat getImage() {
        return mImage;
    }

    public CameraConfig getCameraConfig() {
        return mCameraConfig;
    }

    public <T> T getOptionOrDefault(VisionOption<T> option, T defaultValue) {
        return mVisionServer.getOptionOrDefault(option, defaultValue);
    }
}
