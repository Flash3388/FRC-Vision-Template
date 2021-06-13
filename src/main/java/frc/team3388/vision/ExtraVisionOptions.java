package frc.team3388.vision;

import com.flash3388.flashlib.vision.control.VisionOption;

public class ExtraVisionOptions {

    private ExtraVisionOptions() {}

    public static final VisionOption<Integer> SELECTED_CAMERA = VisionOption.create("selected_camera", Integer.class);

    public static final VisionOption<Integer> HUE_MIN = VisionOption.create("hue_min", Integer.class);
    public static final VisionOption<Integer> HUE_MAX = VisionOption.create("hue_max", Integer.class);
    public static final VisionOption<Integer> SATURATION_MIN = VisionOption.create("saturation_min", Integer.class);
    public static final VisionOption<Integer> SATURATION_MAX = VisionOption.create("saturation__max", Integer.class);
    public static final VisionOption<Integer> VALUE_MIN = VisionOption.create("value_min", Integer.class);
    public static final VisionOption<Integer> VALUE_MAX = VisionOption.create("value_max", Integer.class);

    public static final VisionOption<Double> MIN_ACCEPTED_SCORE = VisionOption.create("min_accepted_score", Double.class);
    public static final VisionOption<Boolean> MULTI_TARGET = VisionOption.create("multi_target", Boolean.class);
}
