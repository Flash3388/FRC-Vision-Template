package frc.team3388.vision.config;

import com.google.gson.JsonObject;
import frc.team3388.vision.VisionType;

public class VisionConfig {

    private final ColorConfig mColorConfig;
    private final VisionType mVisionType;
    private final JsonObject mExtras;

    public VisionConfig(ColorConfig colorConfig, VisionType visionType, JsonObject extras) {
        mColorConfig = colorConfig;
        mVisionType = visionType;
        mExtras = extras;
    }

    public ColorConfig getColorConfig() {
        return mColorConfig;
    }

    public VisionType getVisionType() {
        return mVisionType;
    }

    public JsonObject getExtras() {
        return mExtras;
    }
}
