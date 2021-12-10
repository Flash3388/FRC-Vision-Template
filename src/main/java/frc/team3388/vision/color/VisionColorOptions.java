package frc.team3388.vision.color;

import com.flash3388.flashlib.vision.control.VisionOption;
import com.flash3388.frc.nt.vision.NtVisionServer;
import frc.team3388.vision.ExtraVisionOptions;
import org.opencv.core.Scalar;

import java.util.Arrays;
import java.util.List;

public class VisionColorOptions {

    private final NtVisionServer mServer;
    private final List<ColorDimensionOptions> mDimensions;

    public VisionColorOptions(NtVisionServer server) {
        mServer = server;
        mDimensions = Arrays.asList(
                new ColorDimensionOptions(ExtraVisionOptions.COLOR_DIM1_MIN, ExtraVisionOptions.COLOR_DIM1_MAX),
                new ColorDimensionOptions(ExtraVisionOptions.COLOR_DIM2_MIN, ExtraVisionOptions.COLOR_DIM2_MAX),
                new ColorDimensionOptions(ExtraVisionOptions.COLOR_DIM3_MIN, ExtraVisionOptions.COLOR_DIM3_MAX)
        );
    }

    public ColorSpace getColorSpace() {
        int ordinal = mServer.getOptionOrDefault(ExtraVisionOptions.COLOR_SPACE, 0);
        return ColorSpace.values()[ordinal];
    }

    public Scalar getMin() {
        ColorSpace colorSpace = getColorSpace();

        double[] values = {0, 0, 0, 0};
        for (int i = 0; i < mDimensions.size(); i++) {
            values[i] = mDimensions.get(i).getMin(mServer, colorSpace.getDimensions().get(i));
        }

        return new Scalar(values);
    }

    public Scalar getMax() {
        ColorSpace colorSpace = getColorSpace();

        double[] values = {0, 0, 0, 0};
        for (int i = 0; i < mDimensions.size(); i++) {
            values[i] = mDimensions.get(i).getMax(mServer, colorSpace.getDimensions().get(i));
        }

        return new Scalar(values);
    }

    private static class ColorDimensionOptions {

        private final VisionOption<Integer> mMin;
        private final VisionOption<Integer> mMax;

        public ColorDimensionOptions(VisionOption<Integer> min, VisionOption<Integer> max) {
            mMin = min;
            mMax = max;
        }

        public int getMin(NtVisionServer server, ColorDimension dimension) {
            return server.getOptionOrDefault(mMin, dimension.getValueRange().start);
        }

        public int getMax(NtVisionServer server, ColorDimension dimension) {
            return server.getOptionOrDefault(mMax, dimension.getValueRange().end);
        }
    }
}
