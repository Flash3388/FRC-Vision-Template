package frc.team3388.vision.nt;

import com.flash3388.frc.nt.vision.NtVisionServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.team3388.vision.config.Config;
import org.slf4j.Logger;

public class NtControl implements AutoCloseable {

    private final NetworkTableInstance mInstance;
    private final NtVisionServer mVisionServer;

    public NtControl(Config config, Logger logger) {
        mInstance = NetworkTableInstance.getDefault();
        NtStarter ntStarter = config.getNtConfig().getMode().createStarter();
        ntStarter.start(mInstance, config.getNtConfig(), logger);

        mVisionServer = new NtVisionServer(mInstance.getTable("vision"));
    }

    public NetworkTable getTable(String name) {
        return mInstance.getTable(name);
    }

    public NtVisionServer getVisionServer() {
        return mVisionServer;
    }

    @Override
    public void close() {
        mInstance.close();
    }
}
