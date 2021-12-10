package frc.team3388.vision.config;

import frc.team3388.vision.color.ColorSpace;
import org.opencv.core.Scalar;

public class ColorConfig {

    private final ColorSpace mColorSpace;
    private final Scalar mMin;
    private final Scalar mMax;

    public ColorConfig(ColorSpace colorSpace, Scalar min, Scalar max) {
        mColorSpace = colorSpace;
        mMin = min;
        mMax = max;
    }

    public ColorSpace getColorSpace() {
        return mColorSpace;
    }

    public Scalar getMin() {
        return mMin;
    }

    public Scalar getMax() {
        return mMax;
    }
}
