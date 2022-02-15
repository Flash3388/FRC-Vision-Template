package frc.team3388.vision.user;

import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.cv.processing.Scorable;
import com.flash3388.flashlib.vision.processing.Processor;
import com.flash3388.frc.nt.vision.StandardVisionOptions;
import frc.team3388.vision.VisionData;
import frc.team3388.vision.config.TargetConfig;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public class UserProcessor implements Processor<VisionData, Optional<? extends Scorable>> {

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
    public Optional<? extends Scorable> process(VisionData input) throws VisionException {
        Mat image = input.getImage();
        // CameraConfig cameraConfig = input.getCameraConfig();
        boolean isDebugMode = input.getOptionOrDefault(StandardVisionOptions.DEBUG, false);
        boolean targetFound = false;

        // Additional ideas
        // https://docs.wpilib.org/en/stable/docs/software/vision-processing/wpilibpi/morphological-operations.html

        List<MatOfPoint> contours = mCvProcessing.detectContours(image);
        Optional<RatioTarget> optional = retrieveBestTarget(contours);
        if (optional.isPresent()) {
            RatioTarget target = optional.get();
            if (target.score() > MIN_SCORE) {
                if (isDebugMode) {
                    Imgproc.cvtColor(image, mOutputMat, Imgproc.COLOR_GRAY2RGB);
                    target.drawOn(mOutputMat);
                    mOutputPipeline.process(mOutputMat);

                    mLogger.debug("Found target {}", target);
                }
                targetFound = true;
            }
        }

        if (!targetFound && isDebugMode) {
            Imgproc.cvtColor(image, mOutputMat, Imgproc.COLOR_GRAY2RGB);
            mOutputPipeline.process(mOutputMat);

            mLogger.debug("No target");
        }

        return optional;
    }

    private Optional<RatioTarget> retrieveBestTarget(List<MatOfPoint> contours) {
        return rectifyContours(contours)
                .filter(rect -> rect.area() > MIN_CONTOUR_SIZE)
                .map(rect -> new RatioTarget(rect, mTargetConfig.getDimensionsRatio()))
                .max(Comparator.comparingDouble(RatioTarget::score));
    }

    private Stream<Rect> rectifyContours(List<MatOfPoint> contours) {
        return contours.stream()
                .map(Imgproc::boundingRect);
    }
}
