package frc.team3388.vision;

import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.flashlib.vision.processing.color.HsvColorSettings;
import com.flash3388.frc.nt.vision.NtVisionServer;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.first.networktables.NetworkTable;

import java.util.function.Consumer;

public class Vision {

    private final NtControl mNtControl;
    private final NtVisionServer mVisionServer;
    private final MjpegServer mCameraServer;
    private final Source<VisionData> mSource;

    public Vision(NtControl ntControl, NtVisionServer visionServer, MjpegServer cameraServer, Source<VisionData> source) {
        mNtControl = ntControl;
        mVisionServer = visionServer;
        mCameraServer = cameraServer;
        mSource = source;
    }

    public Source<VisionData> getSource() {
        return mSource;
    }

    public HsvColorSettings configureColorSettings() {
        NetworkTable table = mVisionServer.getSubTable("colors");
        return mNtControl.configureColorSettings(table);
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
