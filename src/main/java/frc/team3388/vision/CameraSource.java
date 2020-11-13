package frc.team3388.vision;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.frc.nt.vision.NtVisionServer;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.VideoSource;
import frc.team3388.vision.config.CameraConfig;
import org.opencv.core.Mat;

import java.util.function.Supplier;

public class CameraSource implements Source<VisionData> {

    private final CvSink mCvSink;
    private final Supplier<CameraConfig> mConfigProperty;
    private final NtVisionServer mVisionServer;

    private final Mat mImage;

    public CameraSource(CvSink cvSink, Supplier<CameraConfig> configProperty, NtVisionServer visionServer) {
        mCvSink = cvSink;
        mConfigProperty = configProperty;
        mVisionServer = visionServer;

        mImage = new Mat();
    }

    public CameraSource(VideoSource camera, CameraConfig config, NtVisionServer visionServer) {
        this(new CvSink("vision-source"), Suppliers.of(config), visionServer);
        mCvSink.setSource(camera);
    }

                        @Override
    public VisionData get() throws VisionException {
        CameraConfig config = mConfigProperty.get();
        mCvSink.grabFrame(mImage);

        return new VisionData(mImage, config, mVisionServer);
    }
}
