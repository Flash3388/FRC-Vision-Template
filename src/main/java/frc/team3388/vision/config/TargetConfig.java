package frc.team3388.vision.config;

import com.google.gson.JsonObject;

public class TargetConfig {

    private final JsonObject mJsonData;

    public TargetConfig(JsonObject jsonData) {
        mJsonData = jsonData;
    }

    public double getRealWidth() {
        return mJsonData.get("realWidth").getAsDouble();
    }

    public double getDimensionsRatio() {
        return mJsonData.get("dimensionsRatio").getAsDouble();
    }
}
