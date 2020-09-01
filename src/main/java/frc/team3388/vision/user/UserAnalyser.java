package frc.team3388.vision.user;

import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.processing.Scorable;
import com.flash3388.flashlib.vision.processing.analysis.Analyser;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.flashlib.vision.processing.analysis.AnalysisAlgorithms;
import frc.team3388.vision.config.CameraConfig;
import frc.team3388.vision.config.TargetConfig;

import java.util.Optional;

public class UserAnalyser implements Analyser<CvImage, Optional<? extends Scorable>> {

    private final CameraConfig mCameraConfig;
    private final TargetConfig mTargetConfig;

    public UserAnalyser(CameraConfig cameraConfig, TargetConfig targetConfig) {
        mCameraConfig = cameraConfig;
        mTargetConfig = targetConfig;
    }

    @Override
    public Optional<Analysis> analyse(CvImage originalInput,
                                      Optional<? extends Scorable> postProcess) throws VisionException {
        if (postProcess.isEmpty()) {
            return Optional.empty();
        }

        Scorable target = postProcess.get();

        double distance = AnalysisAlgorithms.measureDistance(
                originalInput.getWidth(), target.getWidth(),
                mTargetConfig.getRealWidth(), mCameraConfig.getCameraFieldOfViewRadians());
        double angle = AnalysisAlgorithms.calculateHorizontalOffsetDegrees2(
                target.getCenter().x(), originalInput.getWidth(),
                mCameraConfig.getCameraFieldOfViewRadians());

        return Optional.of(new Analysis.Builder()
                .put("distance", distance)
                .put("angle", angle)
                .build());
    }
}
