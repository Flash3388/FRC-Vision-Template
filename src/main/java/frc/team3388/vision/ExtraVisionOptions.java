package frc.team3388.vision;

import com.flash3388.flashlib.vision.control.VisionOption;

public class ExtraVisionOptions {

    private ExtraVisionOptions() {}

    public static final VisionOption<Integer> SELECTED_CAMERA = VisionOption.create("selected_camera", Integer.class);
}
