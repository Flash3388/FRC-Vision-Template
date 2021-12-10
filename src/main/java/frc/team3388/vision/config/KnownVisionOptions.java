package frc.team3388.vision.config;

import com.flash3388.flashlib.vision.control.VisionOption;
import com.flash3388.frc.nt.vision.StandardVisionOptions;
import frc.team3388.vision.ExtraVisionOptions;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class KnownVisionOptions {

    public VisionOption<?> find(String name) {
        for (VisionOption<?> option : getAllOptions()) {
            if (option.name().equals(name)) {
                return option;
            }
        }

        throw new NoSuchElementException(name);
    }

    public List<VisionOption<?>> getAllOptions() {
        return Arrays.asList(
                StandardVisionOptions.DEBUG,
                StandardVisionOptions.EXPOSURE,
                ExtraVisionOptions.SELECTED_CAMERA,
                ExtraVisionOptions.COLOR_DIM1_MAX,
                ExtraVisionOptions.COLOR_DIM1_MIN,
                ExtraVisionOptions.COLOR_DIM2_MAX,
                ExtraVisionOptions.COLOR_DIM2_MIN,
                ExtraVisionOptions.COLOR_DIM3_MAX,
                ExtraVisionOptions.COLOR_DIM3_MIN
        );
    }
}
