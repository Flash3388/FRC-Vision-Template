package frc.team3388.vision.user;

import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.processing.Processor;
import frc.team3388.vision.VisionData;
import org.opencv.core.Range;
import org.opencv.imgproc.Imgproc;

public class ColorProcessor implements Processor<VisionData, VisionData> {

    private final HsvRange mHsvRange;
    private final CvProcessing mCvProcessing;

    public ColorProcessor(HsvRange hsvRange, CvProcessing cvProcessing) {
        mHsvRange = hsvRange;
        mCvProcessing = cvProcessing;
    }

    @Override
    public VisionData process(VisionData input) {
        Range hue = mHsvRange.getHue();
        Range saturation = mHsvRange.getSaturation();
        Range value = mHsvRange.getValue();

        Imgproc.cvtColor(input.getImage(), input.getImage(), Imgproc.COLOR_BGR2HSV);
        mCvProcessing.filterMatColors(input.getImage(), input.getImage(), hue, saturation, value);

        return input;
    }
}
