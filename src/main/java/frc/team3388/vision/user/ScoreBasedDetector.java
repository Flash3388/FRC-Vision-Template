package frc.team3388.vision.user;

import com.flash3388.flashlib.vision.cv.CvProcessing;
import frc.team3388.vision.VisionData;
import frc.team3388.vision.config.TargetConfig;
import frc.team3388.vision.detect.ObjectDetector;
import frc.team3388.vision.detect.ScorableTarget;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScoreBasedDetector implements ObjectDetector {

    private final CvProcessing mCvProcessing;
    private final TargetConfig mTargetConfig;

    public ScoreBasedDetector(CvProcessing cvProcessing, TargetConfig targetConfig) {
        mCvProcessing = cvProcessing;
        mTargetConfig = targetConfig;
    }

    @Override
    public Collection<? extends ScorableTarget> detect(VisionData visionData) {
        Mat image = visionData.getImage();
        //boolean isDebugMode = visionData.getOptionOrDefault(StandardVisionOptions.DEBUG, false);

        // Additional ideas
        // https://docs.wpilib.org/en/stable/docs/software/vision-processing/wpilibpi/morphological-operations.html

        List<MatOfPoint> contours = mCvProcessing.detectContours(image);
        return retrieveTargets(contours);
    }

    private Collection<RatioTarget> retrieveTargets(List<MatOfPoint> contours) {
        return rectifyContours(contours)
                .filter(rect -> rect.area() >= mTargetConfig.getMinSize())
                .map(rect -> new RatioTarget(rect, mTargetConfig.getDimensionsRatio()))
                .filter(target -> target.score() >= mTargetConfig.getMinScore())
                .collect(Collectors.toList());
    }

    private Stream<Rect> rectifyContours(List<MatOfPoint> contours) {
        return contours.stream()
                .map(Imgproc::boundingRect);
    }
}
