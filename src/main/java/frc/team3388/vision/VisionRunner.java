package frc.team3388.vision;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.SourcePoller;

public class VisionRunner {

    private final Source<VisionData> mSource;
    private final Pipeline<VisionData> mPipeline;
    private final ThrowableHandler mThrowableHandler;

    private Thread mVisionThread;

    public VisionRunner(Source<VisionData> source, Pipeline<VisionData> pipeline, ThrowableHandler throwableHandler) {
        mSource = source;
        mPipeline = pipeline;
        mThrowableHandler = throwableHandler;
    }

    public synchronized void start() {
        stop();

        mVisionThread = new Thread(new SourcePoller<>(mSource, mPipeline, mThrowableHandler), "vision-thread");
        mVisionThread.setDaemon(true);
        mVisionThread.start();
    }

    public synchronized void stop() {
        if (mVisionThread != null) {
            mVisionThread.interrupt();
            mVisionThread = null;
        }
    }
}
