package frc.team3388.vision.user;

import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.cv.processing.Scorable;
import com.flash3388.flashlib.vision.processing.Processor;
import com.flash3388.frc.nt.vision.StandardVisionOptions;
import frc.team3388.vision.ExtraVisionOptions;
import frc.team3388.vision.VisionData;
import frc.team3388.vision.config.TargetConfig;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserProcessor implements Processor<VisionData, List<? extends Scorable>> {

    private static final double MIN_CONTOUR_SIZE = 1000;
    private static final double MIN_SCORE = 0.6;

    private final CvProcessing mCvProcessing;
    private final TargetConfig mTargetConfig;
    private final Pipeline<Mat> mOutputPipeline;
    private final Logger mLogger;

    private final Mat mOutputMat;

    public UserProcessor(CvProcessing cvProcessing, TargetConfig targetConfig,
                         Pipeline<Mat> outputPipeline, Logger logger) {
        mCvProcessing = cvProcessing;
        mTargetConfig = targetConfig;
        mOutputPipeline = outputPipeline;
        mLogger = logger;

        mOutputMat = new Mat();
    }

    @Override
    public List<? extends Scorable> process(VisionData input) throws VisionException {
        Mat image = input.getImage();
        // CameraConfig cameraConfig = input.getCameraConfig();
        boolean isDebugMode = input.getOptionOrDefault(StandardVisionOptions.DEBUG, false);
        double minAcceptedScore = input.getOptionOrDefault(ExtraVisionOptions.MIN_ACCEPTED_SCORE, MIN_SCORE);
        boolean multiTarget = input.getOptionOrDefault(ExtraVisionOptions.MULTI_TARGET, false);

        List<MatOfPoint> contours = mCvProcessing.detectContours(image);
        List<RatioTarget> targets = retrieveBestTargets(contours, minAcceptedScore, multiTarget);
        if (!targets.isEmpty()) {
            if (isDebugMode) {
                Imgproc.cvtColor(image, mOutputMat, Imgproc.COLOR_GRAY2RGB);

                for (RatioTarget target : targets) {
                    target.drawOn(mOutputMat);
                }

                mOutputPipeline.process(mOutputMat);

                mLogger.debug("Found targets {}", targets);
            }
        } else if (isDebugMode) {
            Imgproc.cvtColor(image, mOutputMat, Imgproc.COLOR_GRAY2RGB);
            mOutputPipeline.process(mOutputMat);

            mLogger.debug("No target");
        }

        return targets;
    }

    private List<RatioTarget> retrieveBestTargets(List<MatOfPoint> contours, double minAcceptedScore,
                                                  boolean multiTargets) {
        Stream<RatioTarget> stream = rectifyContours(contours)
                .filter(rect -> rect.area() > MIN_CONTOUR_SIZE)
                .map(rect -> new RatioTarget(rect, mTargetConfig.getDimensionsRatio()));
        if (multiTargets) {
            return stream.filter((target) -> target.score() >= minAcceptedScore)
                    .collect(Collectors.toList());
        } else {
            Optional<RatioTarget> best = stream.max(Comparator.comparingDouble(RatioTarget::score));
            if (best.isEmpty()) {
                return Collections.emptyList();
            }

            RatioTarget target = best.get();
            if (target.score() < minAcceptedScore) {
                return Collections.emptyList();
            }

            return Collections.singletonList(target);
        }
    }

    private Stream<Rect> rectifyContours(List<MatOfPoint> contours) {
        return contours.stream()
                .map(Imgproc::boundingRect);
    }
}
