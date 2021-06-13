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
import org.opencv.core.Mat;

import java.util.List;
import java.util.Optional;

public class UserAnalyser implements Analyser<VisionData, List<? extends Scorable>> {

    private final TargetConfig mTargetConfig;

    public UserAnalyser(TargetConfig targetConfig) {
        mTargetConfig = targetConfig;
    }

    @Override
    public Optional<Analysis> analyse(VisionData originalInput,
                                      List<? extends Scorable> postProcess) throws VisionException {
        if (postProcess.isEmpty()) {
            return Optional.empty();
        }

        Mat originalImage = originalInput.getImage();
        CameraConfig cameraConfig = originalInput.getCameraConfig();

        JsonAnalysis.Builder builder = Analysis.builder();

        for (Scorable target : postProcess) {
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
                    .put("distance", distance)
                    .put("angle", angle)
                    .put("score", target.score())
                    .put("centerX", target.getCenter().x())
                    .put("centerY", target.getCenter().y())
                    .build();
        }

        return Optional.of(builder.build());
    }
}
