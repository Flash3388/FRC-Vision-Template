package frc.team3388.vision.user;

import com.flash3388.frc.nt.vision.NtVisionServer;
import frc.team3388.vision.ExtraVisionOptions;
import frc.team3388.vision.config.ColorConfig;
import org.opencv.core.Range;

public class HsvRange {

    private final NtVisionServer mVisionServer;

    private final Range mDefaultHue;
    private final Range mDefaultSaturation;
    private final Range mDefaultValue;

    public HsvRange(NtVisionServer visionServer, Range defaultHue, Range defaultSaturation, Range defaultValue) {
        mVisionServer = visionServer;
        mDefaultHue = defaultHue;
        mDefaultSaturation = defaultSaturation;
        mDefaultValue = defaultValue;
    }

    public HsvRange(NtVisionServer visionServer, ColorConfig colorConfig) {
        this(visionServer, colorConfig.getHue(), colorConfig.getSaturation(), colorConfig.getValue());
    }

    public Range getHue() {
        int min = mVisionServer.getOptionOrDefault(ExtraVisionOptions.HUE_MIN, mDefaultHue.start);
        int max = mVisionServer.getOptionOrDefault(ExtraVisionOptions.HUE_MAX, mDefaultHue.end);

        return new Range(min, max);
    }

    public Range getSaturation() {
        int min = mVisionServer.getOptionOrDefault(ExtraVisionOptions.SATURATION_MIN, mDefaultSaturation.start);
        int max = mVisionServer.getOptionOrDefault(ExtraVisionOptions.SATURATION_MAX, mDefaultSaturation.end);

        return new Range(min, max);
    }

    public Range getValue() {
        int min = mVisionServer.getOptionOrDefault(ExtraVisionOptions.VALUE_MIN, mDefaultValue.start);
        int max = mVisionServer.getOptionOrDefault(ExtraVisionOptions.VALUE_MAX, mDefaultValue.end);

        return new Range(min, max);
    }
}
