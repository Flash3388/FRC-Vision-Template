package frc.team3388.vision;

import com.google.gson.JsonObject;
import frc.team3388.vision.config.ConfigLoadException;

public enum VisionType {
    SINGLE_CAMERA {
        @Override
        public void verifyConfig(JsonObject root) throws ConfigLoadException {
            if (!root.has("camera")) {
                throw new ConfigLoadException("expected 'camera' in vision config for SINGLE_CAMERA type");
            }
        }

        @Override
        public VisionFactory createFactory() {
            return new SingleCameraVisionFactory();
        }
    },
    SWITCHED_CAMERA {
        @Override
        public void verifyConfig(JsonObject root) {

        }

        @Override
        public VisionFactory createFactory() {
            return new SwitchableCameraVisionFactory();
        }
    }
    ;

    public abstract void verifyConfig(JsonObject root) throws ConfigLoadException;
    public abstract VisionFactory createFactory();
}
