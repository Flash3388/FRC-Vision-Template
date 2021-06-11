package frc.team3388.vision.config;

import com.google.gson.JsonObject;

public class VisionConfig {

    private final ColorConfig mColorConfig;
    private final VisionType mVisionType;
    private final boolean mAutoStart;
    private final VisionOptionsConfig mOptionsConfig;
    private final JsonObject mExtras;

    public VisionConfig(ColorConfig colorConfig, VisionType visionType, boolean autoStart, VisionOptionsConfig optionsConfig, JsonObject extras) {
        mColorConfig = colorConfig;
        mVisionType = visionType;
        mAutoStart = autoStart;
        mOptionsConfig = optionsConfig;
        mExtras = extras;
    }

    public ColorConfig getColorConfig() {
        return mColorConfig;
    }

    public VisionType getVisionType() {
        return mVisionType;
    }

    public boolean isAutoStart() {
        return mAutoStart;
    }

    public VisionOptionsConfig getOptionsConfig() {
        return mOptionsConfig;
    }

    public JsonObject getExtras() {
        return mExtras;
    }
}
