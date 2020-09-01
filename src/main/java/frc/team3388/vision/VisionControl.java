package frc.team3388.vision;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.cv.processing.Scorable;
import com.flash3388.flashlib.vision.processing.Processor;
import com.flash3388.flashlib.vision.processing.VisionPipeline;
import com.flash3388.flashlib.vision.processing.color.HsvColorSettings;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.vision.VisionThread;
import frc.team3388.vision.config.CameraConfig;
import frc.team3388.vision.config.TargetConfig;
import frc.team3388.vision.user.ColorProcessor;
import frc.team3388.vision.user.UserAnalyser;
import frc.team3388.vision.user.UserOutput;
import frc.team3388.vision.user.UserPipeline;
import org.opencv.core.Mat;

import java.util.Optional;

public class VisionControl {

    private final CvProcessing mCvProcessing;
    private final CvSource mProcessedOutput;
    private final HsvColorSettings mColorSettings;
    private final NtControl mNtControl;
    private final TargetConfig mTargetConfig;
    private final ThrowableHandler mThrowableHandler;

    public VisionControl(CvProcessing cvProcessing, CvSource processedOutput, HsvColorSettings colorSettings,
                         NtControl ntControl, TargetConfig targetConfig, ThrowableHandler throwableHandler) {
        mCvProcessing = cvProcessing;
        mProcessedOutput = processedOutput;
        mColorSettings = colorSettings;
        mNtControl = ntControl;
        mTargetConfig = targetConfig;
        mThrowableHandler = throwableHandler;
    }

    public void startForCamera(VideoSource camera, CameraConfig cameraConfig) {
        Processor<CvImage, Mat> convertor = CvImage::getMat;
        Processor<Mat, Optional<? extends Scorable>> processor =
                new ColorProcessor(mColorSettings, mCvProcessing)
                    .pipeTo(new UserPipeline(mCvProcessing, cameraConfig, mTargetConfig, mProcessedOutput::putFrame));

        Pipeline<CvImage> imagePipeline = new VisionPipeline.Builder<CvImage, Optional<? extends Scorable>>()
                .process(convertor
                        .pipeTo(processor))
                .analyse(new UserAnalyser(cameraConfig, mTargetConfig))
                .analysisTo(new UserOutput(mNtControl.getResultsTable()))
                .build();

        VisionThread visionThread = new VisionThread(camera,
                (image) -> {
                    try {
                        imagePipeline.process(new CvImage(image));
                    } catch (Throwable t) {
                        mThrowableHandler.handle(t);
                    }
                },
                pipeline -> {
                });

        visionThread.start();
    }
}
