package frc.team3388.vision.control;

import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.frc.nt.vision.NtVisionServer;
import edu.wpi.cscore.MjpegServer;
import frc.team3388.vision.ExtraVisionOptions;
import frc.team3388.vision.VisionData;
import frc.team3388.vision.config.ColorConfig;
import frc.team3388.vision.config.Config;
import frc.team3388.vision.user.HsvRange;

import java.util.function.Consumer;

public class Vision {

    private final Config mConfig;
    private final NtVisionServer mVisionServer;
    private final MjpegServer mCameraServer;
    private final Source<VisionData> mSource;

    public Vision(Config config, NtVisionServer visionServer, MjpegServer cameraServer, Source<VisionData> source) {
        mConfig = config;
        mVisionServer = visionServer;
        mCameraServer = cameraServer;
        mSource = source;
    }

    public Source<VisionData> getSource() {
        return mSource;
    }

    public HsvRange configureColorSettings() {
        ColorConfig colorConfig = mConfig.getVisionConfig().getColorConfig();

        if (!mVisionServer.hasOptionValue(ExtraVisionOptions.HUE_MIN)) {
            mVisionServer.setOption(ExtraVisionOptions.HUE_MIN, colorConfig.getHue().start);
        }
        if (!mVisionServer.hasOptionValue(ExtraVisionOptions.HUE_MAX)) {
            mVisionServer.setOption(ExtraVisionOptions.HUE_MIN, colorConfig.getHue().end);
        }
        if (!mVisionServer.hasOptionValue(ExtraVisionOptions.SATURATION_MIN)) {
            mVisionServer.setOption(ExtraVisionOptions.HUE_MIN, colorConfig.getSaturation().start);
        }
        if (!mVisionServer.hasOptionValue(ExtraVisionOptions.SATURATION_MAX)) {
            mVisionServer.setOption(ExtraVisionOptions.HUE_MIN, colorConfig.getSaturation().end);
        }
        if (!mVisionServer.hasOptionValue(ExtraVisionOptions.VALUE_MIN)) {
            mVisionServer.setOption(ExtraVisionOptions.HUE_MIN, colorConfig.getValue().start);
        }
        if (!mVisionServer.hasOptionValue(ExtraVisionOptions.VALUE_MAX)) {
            mVisionServer.setOption(ExtraVisionOptions.HUE_MIN, colorConfig.getValue().end);
        }

        return new HsvRange(mVisionServer, colorConfig);
    }

    public Consumer<Analysis> getAnalysisConsumer() {
        return mVisionServer::newAnalysis;
    }

    public void configureRunner(VisionRunner runner) {
        mVisionServer.addRunListener((run)-> {
            if (run) {
                runner.start();
            } else {
                runner.stop();
            }
        });
    }
}
