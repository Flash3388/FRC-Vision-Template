package frc.team3388.vision.detect;

import com.flash3388.flashlib.vision.cv.processing.Scorable;
import org.opencv.core.Mat;

public interface ScorableTarget extends Scorable {

    void drawOn(Mat mat);
}
