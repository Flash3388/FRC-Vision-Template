package frc.team3388.vision.config;

import com.google.gson.JsonObject;

public class TargetConfig {

    private final JsonObject mJsonData;

    public TargetConfig(JsonObject jsonData) {
        mJsonData = jsonData;
    }

    public double getDoubleProperty(String name) {
        return mJsonData.get(name).getAsDouble();
    }

    public int getIntProperty(String name) {
        return mJsonData.get(name).getAsInt();
    }

    public double getRealWidth() {
        return getDoubleProperty("realWidth");
    }

    public double getDimensionsRatio() {
        return getDoubleProperty("dimensionsRatio");
    }

    public int getMinSize() {
        return getIntProperty("minSize");
    }

    public double getMinScore() {
        return getDoubleProperty("minScore");
    }
}
