package frc.team3388.vision.user;

import com.flash3388.flashlib.vision.cv.processing.Scorable;
import com.jmath.vectors.Vector2;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class RatioTarget implements Scorable {

    private final Rect mRect;
    private final double mExpectedDimensionRatio;

    public RatioTarget(Rect rect, double expectedDimensionRatio) {
        mRect = rect;
        mExpectedDimensionRatio = expectedDimensionRatio;
    }

    @Override
    public double getWidth() {
        return mRect.width;
    }

    @Override
    public double getHeight() {
        return mRect.height;
    }

    @Override
    public Vector2 getCenter() {
        return new Vector2(
                mRect.x + mRect.width * 0.5,
                mRect.y + mRect.height * 0.5
        );
    }

    @Override
    public double score() {
        double ratio = mRect.height / (double) mRect.width;
        return ratio > mExpectedDimensionRatio ?
                mExpectedDimensionRatio / ratio :
                ratio / mExpectedDimensionRatio;
    }

    public void drawOn(Mat mat) {
        Imgproc.rectangle(mat, mRect.tl(), mRect.br(), new Scalar(78, 150, 200), 2);
    }

    @Override
    public String toString() {
        Vector2 center = getCenter();
        return String.format("RatioTarget{x=%.1f,y=%.1f,score=%.3f}",
                center.x(),
                center.y(),
                score());
    }
}
