package frc.team3388.vision.color;

import org.opencv.core.Range;

public enum ColorDimension {
    RED(0, 255),
    GREEN(0, 255),
    BLUE(0, 255),
    HUE(0, 180),
    SATURATION(0, 255),
    VALUE(0, 255)
    ;

    private final Range mValueRange;

    ColorDimension(Range valueRange) {
        mValueRange = valueRange;
    }

    ColorDimension(int min, int max) {
        this(new Range(min, max));
    }

    public Range getValueRange() {
        return mValueRange;
    }
}
