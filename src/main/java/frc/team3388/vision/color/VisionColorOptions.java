package frc.team3388.vision.color;

import com.flash3388.flashlib.vision.control.VisionOption;
import com.flash3388.frc.nt.vision.NtVisionServer;
import frc.team3388.vision.ExtraVisionOptions;
import frc.team3388.vision.config.ColorConfig;
import org.opencv.core.Scalar;

import java.util.Arrays;
import java.util.List;

public class VisionColorOptions {

    private final NtVisionServer mServer;
    private final List<ColorDimensionOptions> mDimensions;

    public VisionColorOptions(NtVisionServer server, ColorConfig colorConfig) {
        mServer = server;
        mDimensions = Arrays.asList(
                new ColorDimensionOptions(ExtraVisionOptions.COLOR_DIM1_MIN, ExtraVisionOptions.COLOR_DIM1_MAX),
                new ColorDimensionOptions(ExtraVisionOptions.COLOR_DIM2_MIN, ExtraVisionOptions.COLOR_DIM2_MAX),
                new ColorDimensionOptions(ExtraVisionOptions.COLOR_DIM3_MIN, ExtraVisionOptions.COLOR_DIM3_MAX)
        );

        if (!server.hasOptionValue(ExtraVisionOptions.COLOR_SPACE)) {
            server.setOption(ExtraVisionOptions.COLOR_SPACE, colorConfig.getColorSpace().ordinal());
        }

        for (int i = 0; i < mDimensions.size(); i++) {
            mDimensions.get(i).set(
                    server,
                    (int) colorConfig.getMin().val[i],
                    (int) colorConfig.getMax().val[i]
            );
        }
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

        public void set(NtVisionServer server, int min, int max) {
            if (!server.hasOptionValue(mMin)) {
                server.setOption(mMin, min);
            }

            if (!server.hasOptionValue(mMax)) {
                server.setOption(mMax, max);
            }
        }
    }
}
