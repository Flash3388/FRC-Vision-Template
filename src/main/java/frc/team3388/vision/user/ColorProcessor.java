package frc.team3388.vision.user;

import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.processing.Processor;
import frc.team3388.vision.VisionData;
import frc.team3388.vision.color.ColorSpace;
import frc.team3388.vision.color.NtColorRange;
import org.opencv.core.Range;
import org.opencv.imgproc.Imgproc;

public class ColorProcessor implements Processor<VisionData, VisionData> {

    private final NtColorRange mColorRange;
    private final CvProcessing mCvProcessing;

    public ColorProcessor(NtColorRange colorRange, CvProcessing cvProcessing) {
        mColorRange = colorRange;
        mCvProcessing = cvProcessing;
    }

    @Override
    public VisionData process(VisionData input) {
        // hue: dim_?, saturation: dim_? value: dim_?
        mColorRange.convertColorSpace(input.getImage(), ColorSpace.BGR, input.getImage());
        mColorRange.filterColors(input.getImage(), input.getImage());

        return input;
    }
}
