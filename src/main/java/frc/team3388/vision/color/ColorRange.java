package frc.team3388.vision.color;

import org.opencv.core.Range;

import java.util.Collections;
import java.util.List;

public class ColorRange {

    private final ColorSpace mColorSpace;
    private final List<Range> mDimensions;

    public ColorRange(ColorSpace colorSpace, List<Range> dimensions) {
        mColorSpace = colorSpace;
        mDimensions = Collections.unmodifiableList(dimensions);
    }

    public ColorSpace getColorSpace() {
        return mColorSpace;
    }

    public List<Range> getDimensions() {
        return mDimensions;
    }
}
