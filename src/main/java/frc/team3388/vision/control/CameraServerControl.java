package frc.team3388.vision.control;

import com.castle.util.closeables.Closer;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import org.opencv.core.Mat;

public class CameraServerControl implements AutoCloseable {

    private final MjpegServer mMainCamera;
    private final CvSource mPostProcess;

    public CameraServerControl() {
        CameraServer cameraServer = CameraServer.getInstance();
        mMainCamera = cameraServer.addSwitchedCamera("camera");
        mPostProcess = cameraServer.putVideo("post-process", 320, 480);
    }

    public void setMainCameraSource(VideoSource source) {
        mMainCamera.setSource(source);
    }

    public void putPostProcess(Mat mat) {
        mPostProcess.putFrame(mat);
    }

    @Override
    public void close() throws Exception {
        Closer closer = new Closer();
        closer.add(mMainCamera);
        closer.add(mPostProcess);

        closer.close();
    }
}
