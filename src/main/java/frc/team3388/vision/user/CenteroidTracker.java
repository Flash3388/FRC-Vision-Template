package frc.team3388.vision.user;

import com.jmath.vectors.Vector2;
import frc.team3388.vision.detect.ObjectTracker;
import frc.team3388.vision.detect.ScorableTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CenteroidTracker implements ObjectTracker {

    private final Map<Integer, ScorableTarget> mTrackedObjects;
    private int mNextId;

    public CenteroidTracker() {
        mTrackedObjects = new HashMap<>();
        mNextId = 1;
    }

    @Override
    public Map<Integer, ? extends ScorableTarget> updateTracked(Collection<? extends ScorableTarget> objects) {
        List<ScorableTarget> targetsList = new LinkedList<>(objects);
        Map<Integer, ScorableTarget> updates = new HashMap<>();
        Set<Integer> knownIds = new HashSet<>(mTrackedObjects.keySet());

        for (Map.Entry<Integer, ScorableTarget> entry : mTrackedObjects.entrySet()) {
            List<TrackPair> trackPairs = new ArrayList<>();
            for (int i = 0; i < targetsList.size(); i++) {
                trackPairs.add(new TrackPair(entry.getValue(), i, targetsList.get(i)));
            }

            if (trackPairs.isEmpty()) {
                continue;
            }

            // we want the smallest distance match, which will be the first after sorting
            trackPairs.sort(Comparator.comparingDouble(TrackPair::distance));
            TrackPair pair = trackPairs.get(0);
            updates.put(entry.getKey(), pair.mUpdated);
            // we found the target so let's remove it from the list so we don't use it anymore
            targetsList.remove(pair.mIndex);
            // mark the id as matched
            knownIds.remove(entry.getKey());
        }

        // update changes in tracked objects
        mTrackedObjects.putAll(updates);

        // handle lost objects
        for (int id : knownIds) {
            mTrackedObjects.remove(id);
        }

        // add new objects
        for (ScorableTarget target : targetsList) {
            int id = mNextId++;
            mTrackedObjects.put(id, target);
        }

        return Collections.unmodifiableMap(mTrackedObjects);
    }

    private static class TrackPair {

        final ScorableTarget mOriginal;
        final int mIndex;
        final ScorableTarget mUpdated;

        private TrackPair(ScorableTarget original, int index, ScorableTarget updated) {
            mOriginal = original;
            mIndex = index;
            mUpdated = updated;
        }

        double distance() {
            Vector2 originalCenter = mOriginal.getCenter();
            Vector2 updatedCenter = mUpdated.getCenter();

            return Math.sqrt(Math.pow(updatedCenter.x() - originalCenter.x(), 2) +
                    Math.pow(updatedCenter.y() - originalCenter.y(), 2));
        }
    }
}
