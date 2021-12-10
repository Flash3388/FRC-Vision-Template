package frc.team3388.vision.config;

import java.util.Collections;
import java.util.List;

public class Config {

    private final List<CameraConfig> mCameraConfigs;
    private final NtConfig mNtConfig;
    private final VisionConfig mVisionConfig;
    private final TargetConfig mTargetConfig;

    public Config(List<CameraConfig> cameraConfigs, NtConfig ntConfig,
                  VisionConfig visionConfig, TargetConfig targetConfig) {
        mCameraConfigs = Collections.unmodifiableList(cameraConfigs);
        mNtConfig = ntConfig;
        mVisionConfig = visionConfig;
        mTargetConfig = targetConfig;
    }

    public List<CameraConfig> getCameraConfigs() {
        return mCameraConfigs;
    }

    public NtConfig getNtConfig() {
        return mNtConfig;
    }

    public VisionConfig getVisionConfig() {
        return mVisionConfig;
    }

    public TargetConfig getTargetConfig() {
        return mTargetConfig;
    }
}
