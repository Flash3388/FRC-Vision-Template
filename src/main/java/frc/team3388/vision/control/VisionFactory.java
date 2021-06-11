package frc.team3388.vision.control;

import frc.team3388.vision.config.Config;

public interface VisionFactory {

    Vision create(Config config, Controls controls);
}
