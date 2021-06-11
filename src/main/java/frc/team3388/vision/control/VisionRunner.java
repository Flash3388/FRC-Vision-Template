package frc.team3388.vision.control;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.PeriodicPoller;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.Source;
import frc.team3388.vision.VisionData;

public class VisionRunner {

    private final Source<VisionData> mSource;
    private final Pipeline<VisionData> mPipeline;
    private final Time mPeriod;
    private final ThrowableHandler mThrowableHandler;

    private Thread mVisionThread;

    public VisionRunner(Source<VisionData> source, Pipeline<VisionData> pipeline, Time period,
                        ThrowableHandler throwableHandler) {
        mSource = source;
        mPipeline = pipeline;
        mPeriod = period;
        mThrowableHandler = throwableHandler;
    }

    public synchronized void start() {
        stop();

        mVisionThread = new Thread(new PeriodicPoller<>(mSource, mPeriod, mPipeline, mThrowableHandler), "vision-thread");
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
