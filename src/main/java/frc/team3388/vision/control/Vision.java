package frc.team3388.vision.control;

import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.frc.nt.vision.NtVisionServer;
import frc.team3388.vision.ExtraVisionOptions;
import frc.team3388.vision.VisionData;
import frc.team3388.vision.config.ColorConfig;
import frc.team3388.vision.config.Config;
import frc.team3388.vision.color.NtColorRange;

import java.util.function.Consumer;

public class Vision {

    private final Source<VisionData> mSource;
    private final NtVisionServer mVisionServer;
    private final Config mConfig;

    public Vision(Source<VisionData> source, NtVisionServer visionServer, Config config) {
        mSource = source;
        mVisionServer = visionServer;
        mConfig = config;
    }

    public Source<VisionData> getSource() {
        return mSource;
    }

    public NtColorRange configureColorSettings() {
        ColorConfig colorConfig = mConfig.getVisionConfig().getColorConfig();

        if (!mVisionServer.hasOptionValue(ExtraVisionOptions.COLOR_DIM1_MIN)) {
            mVisionServer.setOption(ExtraVisionOptions.COLOR_DIM1_MIN, colorConfig.getHue().start);
        }
        if (!mVisionServer.hasOptionValue(ExtraVisionOptions.COLOR_DIM1_MAX)) {
            mVisionServer.setOption(ExtraVisionOptions.COLOR_DIM1_MAX, colorConfig.getHue().end);
        }
        if (!mVisionServer.hasOptionValue(ExtraVisionOptions.COLOR_DIM2_MIN)) {
            mVisionServer.setOption(ExtraVisionOptions.COLOR_DIM2_MIN, colorConfig.getSaturation().start);
        }
        if (!mVisionServer.hasOptionValue(ExtraVisionOptions.COLOR_DIM2_MAX)) {
            mVisionServer.setOption(ExtraVisionOptions.COLOR_DIM2_MAX, colorConfig.getSaturation().end);
        }
        if (!mVisionServer.hasOptionValue(ExtraVisionOptions.COLOR_DIM3_MIN)) {
            mVisionServer.setOption(ExtraVisionOptions.COLOR_DIM3_MIN, colorConfig.getValue().start);
        }
        if (!mVisionServer.hasOptionValue(ExtraVisionOptions.COLOR_DIM3_MAX)) {
            mVisionServer.setOption(ExtraVisionOptions.COLOR_DIM3_MAX, colorConfig.getValue().end);
        }

        return new NtColorRange(mVisionServer);
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
