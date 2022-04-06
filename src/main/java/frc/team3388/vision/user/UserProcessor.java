package frc.team3388.vision.user;

import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.processing.Processor;
import com.flash3388.frc.nt.vision.StandardVisionOptions;
import frc.team3388.vision.VisionData;
import frc.team3388.vision.config.TargetConfig;
import frc.team3388.vision.detect.ObjectDetector;
import frc.team3388.vision.detect.ObjectTracker;
import frc.team3388.vision.detect.ScorableTarget;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;


public class UserProcessor implements Processor<VisionData, Optional<? extends Map<Integer, ? extends ScorableTarget>>> {

    private final CvProcessing mCvProcessing;
    private final TargetConfig mTargetConfig;
    private final Pipeline<Mat> mOutputPipeline;
    private final Logger mLogger;

    private final ObjectDetector mObjectDetector;
    private final ObjectTracker mObjectTracker;
    private final Mat mOutputMat;

    public UserProcessor(CvProcessing cvProcessing, TargetConfig targetConfig,
                         Pipeline<Mat> outputPipeline, Logger logger) {
        mCvProcessing = cvProcessing;
        mTargetConfig = targetConfig;
        mOutputPipeline = outputPipeline;
        mLogger = logger;

        mObjectDetector = new ScoreBasedDetector(cvProcessing, targetConfig);
        mObjectTracker = new CenteroidTracker();

        mOutputMat = new Mat();
    }

    @Override
    public Optional<? extends Map<Integer, ? extends ScorableTarget>> process(VisionData input) throws VisionException {
        Mat image = input.getImage();
        // CameraConfig cameraConfig = input.getCameraConfig();
        boolean isDebugMode = input.getOptionOrDefault(StandardVisionOptions.DEBUG, false);

        Collection<? extends ScorableTarget> objects = mObjectDetector.detect(input);
        Map<Integer, ? extends ScorableTarget> targets = mObjectTracker.updateTracked(objects);

        // Additional ideas
        // https://docs.wpilib.org/en/stable/docs/software/vision-processing/wpilibpi/morphological-operations.html

        if (targets.isEmpty() && isDebugMode) {
            Imgproc.cvtColor(image, mOutputMat, Imgproc.COLOR_GRAY2RGB);
            mOutputPipeline.process(mOutputMat);

            mLogger.debug("No targets");
        } else if (isDebugMode) {
            Imgproc.cvtColor(image, mOutputMat, Imgproc.COLOR_GRAY2RGB);

            for (Map.Entry<Integer, ? extends ScorableTarget> entry : targets.entrySet()) {
                ScorableTarget target = entry.getValue();
                Point center = new Point(target.getCenter().x(), target.getCenter().y());

                target.drawOn(mOutputMat);
                Imgproc.putText(mOutputMat, String.valueOf(entry.getKey()), center,
                        Imgproc.FONT_HERSHEY_PLAIN, 1, new Scalar(23, 35, 100));
                mLogger.debug("Found target (id={}) {}", entry.getKey(), target);
            }

            mOutputPipeline.process(mOutputMat);
        }

        return Optional.of(targets);
    }
}
