package frc.team3388.vision.color;

import com.flash3388.frc.nt.vision.NtVisionServer;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class NtColorRange {

    private final VisionColorOptions mColorOptions;

    public NtColorRange(NtVisionServer visionServer) {
        mColorOptions = new VisionColorOptions(visionServer);
    }

    public void convertColorSpace(Mat src, ColorSpace srcColorSpace, Mat dst) {
        ColorSpace dstColorSpace = mColorOptions.getColorSpace();

        if (srcColorSpace != dstColorSpace) {
            int code = getColorSpaceConversionCode(srcColorSpace, dstColorSpace);
            Imgproc.cvtColor(src, dst, code);
        }
    }

    public void filterColors(Mat src, Mat dst) {
        Core.inRange(src,
                mColorOptions.getMin(),
                mColorOptions.getMax(),
                dst);
    }

    private int getColorSpaceConversionCode(ColorSpace src, ColorSpace dst) {
        if (src == ColorSpace.BGR && dst == ColorSpace.HSV) {
            return Imgproc.COLOR_BGR2HSV;
        }
        if (src == ColorSpace.BGR && dst == ColorSpace.RGB) {
            return Imgproc.COLOR_BGR2RGB;
        }

        throw new UnsupportedOperationException("not code for color space conversion");
    }
}
