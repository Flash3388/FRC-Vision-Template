package frc.team3388.vision;

import com.flash3388.flashlib.vision.control.VisionOption;

public class ExtraVisionOptions {

    private ExtraVisionOptions() {}

    public static final VisionOption<Integer> SELECTED_CAMERA = VisionOption.create("selected_camera", Integer.class);

    public static final VisionOption<Integer> COLOR_SPACE = VisionOption.create("color_space", Integer.class);
    public static final VisionOption<Integer> COLOR_DIM1_MIN = VisionOption.create("color_dim1_min", Integer.class);
    public static final VisionOption<Integer> COLOR_DIM1_MAX = VisionOption.create("color_dim1_max", Integer.class);
    public static final VisionOption<Integer> COLOR_DIM2_MIN = VisionOption.create("color_dim2_min", Integer.class);
    public static final VisionOption<Integer> COLOR_DIM2_MAX = VisionOption.create("color_dim2_max", Integer.class);
    public static final VisionOption<Integer> COLOR_DIM3_MIN = VisionOption.create("color_dim3_min", Integer.class);
    public static final VisionOption<Integer> COLOR_DIM3_MAX = VisionOption.create("color_dim3_max", Integer.class);
}
