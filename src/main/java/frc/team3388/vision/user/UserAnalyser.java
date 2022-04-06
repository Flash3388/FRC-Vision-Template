package frc.team3388.vision.user;

import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.analysis.Analyser;
import com.flash3388.flashlib.vision.analysis.Analysis;
import com.flash3388.flashlib.vision.analysis.AnalysisAlgorithms;
import com.flash3388.flashlib.vision.analysis.JsonAnalysis;
import com.flash3388.flashlib.vision.cv.processing.Scorable;
import frc.team3388.vision.VisionData;
import frc.team3388.vision.config.CameraConfig;
import frc.team3388.vision.config.TargetConfig;
import frc.team3388.vision.detect.ScorableTarget;
import org.opencv.core.Mat;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class UserAnalyser implements Analyser<VisionData, Optional<? extends Map<Integer, ? extends ScorableTarget>>> {

    private final TargetConfig mTargetConfig;

    public UserAnalyser(TargetConfig targetConfig) {
        mTargetConfig = targetConfig;
    }

    @Override
    public Optional<Analysis> analyse(VisionData originalInput,
                                      Optional<? extends Map<Integer, ? extends ScorableTarget>> postProcess)
            throws VisionException {
        if (postProcess.isEmpty()) {
            return Optional.empty();
        }

        Mat originalImage = originalInput.getImage();
        CameraConfig cameraConfig = originalInput.getCameraConfig();
        Map<Integer, ? extends ScorableTarget> targets = postProcess.get();

        JsonAnalysis.Builder builder = new JsonAnalysis.Builder();

        for (Map.Entry<Integer, ? extends ScorableTarget> entry : targets.entrySet()) {
            ScorableTarget target = entry.getValue();

            double distance = AnalysisAlgorithms.measureDistance(
                    originalImage.width(),
                    target.getWidth(),
                    mTargetConfig.getRealWidth(),
                    cameraConfig.getCameraFieldOfViewRadians());
            double angle = AnalysisAlgorithms.calculateHorizontalOffsetDegrees2(
                    target.getCenter().x(),
                    originalImage.width(),
                    cameraConfig.getCameraFieldOfViewRadians());

            builder.buildTarget()
                    .put("id", entry.getKey())
                    .put("distance", distance)
                    .put("angle", angle)
                    .build();
        }

        return Optional.of(builder.build());
    }
}
