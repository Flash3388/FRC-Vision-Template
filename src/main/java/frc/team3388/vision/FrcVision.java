package frc.team3388.vision;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.cv.processing.Scorable;
import com.flash3388.flashlib.vision.processing.Processor;
import com.flash3388.flashlib.vision.processing.VisionPipeline;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import frc.team3388.vision.config.Config;
import frc.team3388.vision.control.CameraControl;
import frc.team3388.vision.control.NtControl;
import frc.team3388.vision.control.Vision;
import frc.team3388.vision.control.VisionFactory;
import frc.team3388.vision.control.VisionRunner;
import frc.team3388.vision.user.ColorProcessor;
import frc.team3388.vision.user.HsvRange;
import frc.team3388.vision.user.UserAnalyser;
import frc.team3388.vision.user.UserProcessor;

import java.util.List;
import java.util.Optional;

public class FrcVision {

    private final Config mConfig;
    private final CameraControl mCameraControl;
    private final NtControl mNtControl;
    private final VisionFactory mVisionFactory;
    private final ThrowableHandler mThrowableHandler;

    // so they won't be garbage collected
    private Vision mVision;
    private VisionRunner mVisionRunner;

    public FrcVision(Config config, CameraControl cameraControl, NtControl ntControl, VisionFactory visionFactory,
                     ThrowableHandler throwableHandler) {
        mConfig = config;
        mCameraControl = cameraControl;
        mNtControl = ntControl;
        mVisionFactory = visionFactory;
        mThrowableHandler = throwableHandler;
    }

    public void run() {
        mNtControl.startNetworkTables();
        List<VideoSource> cameras = mCameraControl.startCameras();

        if (cameras.size() >= 1) {
            startVisionThread(cameras, mConfig);
            waitForever();
        } else {
            System.out.println("No cameras");
        }
    }

    private void startVisionThread(List<VideoSource> cameras, Config config) {
        Vision vision = mVisionFactory.create(config, cameras, mNtControl);
        mVision = vision;

        CvSource postProcessed = CameraServer.getInstance().putVideo("processed", 480, 320);
        Source<VisionData> source = vision.getSource();
        HsvRange colorSettings = vision.configureColorSettings();
        CvProcessing cvProcessing = new CvProcessing();

        Processor<VisionData, Optional<? extends Scorable>> processor = new ColorProcessor(colorSettings, cvProcessing)
                .andThen(new UserProcessor(cvProcessing, config.getTargetConfig(), postProcessed::putFrame));

        Pipeline<VisionData> imagePipeline = new VisionPipeline.Builder<VisionData, Optional<? extends Scorable>>()
                .process(processor)
                .analyse(new UserAnalyser(config.getTargetConfig()))
                .analysisTo(vision.getAnalysisConsumer())
                .build();

        VisionRunner runner = new VisionRunner(source, imagePipeline, mThrowableHandler);
        vision.configureRunner(runner);
    }

    private static void waitForever() {
        for (; ; ) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                return;
            }
        }
    }
}
