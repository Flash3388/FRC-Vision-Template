package frc.team3388.vision.config;

import java.util.Collections;
import java.util.List;

public class Config {

    private final int mTeam;
    private final NtMode mNtMode;
    private final List<CameraConfig> mCameraConfigs;
    private final VisionConfig mVisionConfig;
    private final TargetConfig mTargetConfig;

    public Config(int team, NtMode ntMode, List<CameraConfig> cameraConfigs,
                  VisionConfig visionConfig, TargetConfig targetConfig) {
        mTeam = team;
        mNtMode = ntMode;
        mCameraConfigs = Collections.unmodifiableList(cameraConfigs);
        mVisionConfig = visionConfig;
        mTargetConfig = targetConfig;
    }

    public int getTeamNumber() {
        return mTeam;
    }

    public NtMode getNtMode() {
        return mNtMode;
    }

    public List<CameraConfig> getCameraConfigs() {
        return mCameraConfigs;
    }

    public VisionConfig getVisionConfig() {
        return mVisionConfig;
    }

    public TargetConfig getTargetConfig() {
        return mTargetConfig;
    }
}
