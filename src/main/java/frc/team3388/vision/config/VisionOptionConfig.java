package frc.team3388.vision.config;

import com.flash3388.flashlib.vision.control.VisionOption;

public class VisionOptionConfig<T> {

    private final VisionOption<T> mOption;
    private final T mValue;

    public VisionOptionConfig(VisionOption<T> option, T value) {
        mOption = option;
        mValue = value;
    }

    public VisionOption<T> getOption() {
        return mOption;
    }

    public T getValue() {
        return mValue;
    }
}
