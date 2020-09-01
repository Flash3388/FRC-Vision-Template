package frc.team3388.vision.wpi;

import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;
import edu.wpi.cscore.CvSource;
import org.opencv.core.Mat;

public class CvSourcePipeline implements Pipeline<Mat> {

    private final CvSource mCvSource;

    public CvSourcePipeline(CvSource cvSource) {
        mCvSource = cvSource;
    }

    @Override
    public void process(Mat input) throws VisionException {
        mCvSource.putFrame(input);
    }
}
