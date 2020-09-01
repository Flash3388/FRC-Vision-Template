package frc.team3388.vision.user;

import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.processing.Processor;
import com.flash3388.flashlib.vision.processing.color.ColorRange;
import com.flash3388.flashlib.vision.processing.color.HsvColorSettings;
import org.opencv.core.Mat;
import org.opencv.core.Range;

public class ColorProcessor implements Processor<Mat, Mat> {

    private final HsvColorSettings mHsvColorSettings;
    private final CvProcessing mCvProcessing;

    public ColorProcessor(HsvColorSettings hsvColorSettings, CvProcessing cvProcessing) {
        mHsvColorSettings = hsvColorSettings;
        mCvProcessing = cvProcessing;
    }

    @Override
    public Mat process(Mat input) {
        Range hue = colorRangeToRange(mHsvColorSettings.getHue());
        Range saturation = colorRangeToRange(mHsvColorSettings.getSaturation());
        Range value = colorRangeToRange(mHsvColorSettings.getValue());

        mCvProcessing.filterMatColors(input, input, hue, saturation, value);

        return input;
    }

    private Range colorRangeToRange(ColorRange colorRange) {
        return new Range(colorRange.getMin(), colorRange.getMax());
    }
}
