package frc.team3388.vision.user;

import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.cv.processing.Scorable;
import com.flash3388.flashlib.vision.processing.analysis.Analyser;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.flashlib.vision.processing.analysis.AnalysisAlgorithms;
import frc.team3388.vision.VisionData;
import frc.team3388.vision.config.CameraConfig;
import frc.team3388.vision.config.TargetConfig;
import org.opencv.core.Mat;

import java.util.Optional;

public class UserAnalyser implements Analyser<VisionData, Optional<? extends Scorable>> {

    private final TargetConfig mTargetConfig;

    public UserAnalyser(TargetConfig targetConfig) {
        mTargetConfig = targetConfig;
    }

    @Override
    public Optional<Analysis> analyse(VisionData originalInput,
                                      Optional<? extends Scorable> postProcess) throws VisionException {
        if (postProcess.isEmpty()) {
            return Optional.empty();
        }

        Mat originalImage = originalInput.getImage();
        CameraConfig cameraConfig = originalInput.getCameraConfig();
        Scorable target = postProcess.get();

        double distance = AnalysisAlgorithms.measureDistance(
                originalImage.width(),
                target.getWidth(),
                mTargetConfig.getRealWidth(),
                cameraConfig.getCameraFieldOfViewRadians());

        double angle = AnalysisAlgorithms.calculateHorizontalOffsetDegrees2(
                target.getCenter().x(),
                originalImage.width(),
                cameraConfig.getCameraFieldOfViewRadians());

        return Optional.of(new Analysis.Builder()
                .put("distance", distance)
                .put("angle", angle)
                .build());
    }
}
