package frc.team3388.vision.detect;

import java.util.Collection;
import java.util.Map;

public interface ObjectTracker {

    Map<Integer, ? extends ScorableTarget> updateTracked(Collection<? extends ScorableTarget> objects);
}
