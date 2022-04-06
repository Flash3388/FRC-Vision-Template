package frc.team3388.vision.detect;

import frc.team3388.vision.VisionData;

import java.util.Collection;

public interface ObjectDetector {

    Collection<? extends ScorableTarget> detect(VisionData visionData);
}
