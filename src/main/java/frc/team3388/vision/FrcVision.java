package frc.team3388.vision;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.FilterPipeline;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.cv.processing.Scorable;
import com.flash3388.flashlib.vision.processing.Processor;
import com.flash3388.flashlib.vision.processing.VisionPipeline;
import frc.team3388.vision.config.Config;
import frc.team3388.vision.control.Controls;
import frc.team3388.vision.control.Vision;
import frc.team3388.vision.control.VisionRunner;
import frc.team3388.vision.user.ColorProcessor;
import frc.team3388.vision.user.HsvRange;
import frc.team3388.vision.user.UserAnalyser;
import frc.team3388.vision.user.UserProcessor;
import org.opencv.core.Mat;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class FrcVision {

    private static final Time POLL_PERIOD = Time.milliseconds(5);

    private final Controls mControls;
    private final Vision mVision;
    private final Config mConfig;
    private final Logger mLogger;
    private final ThrowableHandler mThrowableHandler;

    public FrcVision(Controls controls, Vision vision, Config config,
                     Logger logger, ThrowableHandler throwableHandler) {
        mControls = controls;
        mVision = vision;
        mConfig = config;
        mLogger = logger;
        mThrowableHandler = throwableHandler;
    }

    public void run() {
        try (VisionRunner runner = startVisionThread()) {
            waitForever();
        }
    }

    private VisionRunner startVisionThread() {
        Source<VisionData> source = mVision.getSource();
        HsvRange colorSettings = mVision.configureColorSettings();
        CvProcessing cvProcessing = new CvProcessing();

        Pipeline<Mat> postProcess = new FilterPipeline<>(
                (mat)-> !mat.empty(),
                mControls.getServerControl()::putPostProcess);

        Processor<VisionData, List<? extends Scorable>> processor = new ColorProcessor(colorSettings, cvProcessing)
                .andThen(new UserProcessor(cvProcessing, mConfig.getTargetConfig(),
                        postProcess, mLogger));

        Pipeline<VisionData> imagePipeline = new VisionPipeline.Builder<VisionData, List<? extends Scorable>>()
                .process(processor)
                .analyse(new UserAnalyser(mConfig.getTargetConfig()))
                .analysisTo(mVision.getAnalysisConsumer())
                .build();
        imagePipeline = new FilterPipeline<>(
                (data)-> !data.getImage().empty(),
                imagePipeline);

        VisionRunner runner = new VisionRunner(source, imagePipeline, POLL_PERIOD, mThrowableHandler);
        mVision.configureRunner(runner);

        if (mConfig.getVisionConfig().isAutoStart()) {
            runner.start();
        }

        return runner;
    }

    private void waitForever() {
        CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new LoopReleaseThread(latch));

        mLogger.debug("Entering wait loop");
        for (;;) {
            try {
                if (latch.await(10000, TimeUnit.MILLISECONDS)) {
                    mLogger.debug("Latch signaled stop");
                    break;
                }
            } catch (InterruptedException ex) {
                mLogger.debug("Wait interrupted");
                break;
            }
        }
        mLogger.debug("Done wait loop");
    }

    private static class LoopReleaseThread extends Thread {

        private final CountDownLatch mLatch;

        private LoopReleaseThread(CountDownLatch latch) {
            mLatch = latch;
        }

        @Override
        public void run() {
            mLatch.countDown();
        }
    }
}
