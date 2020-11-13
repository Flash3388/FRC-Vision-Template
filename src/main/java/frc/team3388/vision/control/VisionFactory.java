package frc.team3388.vision.control;

import edu.wpi.cscore.VideoSource;
import frc.team3388.vision.config.Config;

import java.util.List;

public interface VisionFactory {

    Vision create(Config config, List<VideoSource> cameras, NtControl ntControl);
}
