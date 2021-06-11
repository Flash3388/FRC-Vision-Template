package frc.team3388.vision.control;

import com.flash3388.frc.nt.vision.NtVisionServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.team3388.vision.config.Config;
import frc.team3388.vision.config.NtMode;
import org.slf4j.Logger;

public class NtControl implements AutoCloseable {

    private final NetworkTableInstance mInstance;
    private final NtVisionServer mVisionServer;

    public NtControl(Config config, Logger logger) {
        mInstance = NetworkTableInstance.getDefault();
        startNetworkTables(config, logger);

        mVisionServer = new NtVisionServer(mInstance.getTable("vision"));
    }

    public NetworkTable getTable(String name) {
        return mInstance.getTable(name);
    }

    public NtVisionServer getVisionServer() {
        return mVisionServer;
    }

    private void startNetworkTables(Config config, Logger logger) {
        if (config.getNtMode() == NtMode.SERVER) {
            logger.info("Setting up NetworkTables server");
            mInstance.startServer();
        } else {
            logger.info("Setting up NetworkTables client for team {}", config.getTeamNumber());
            mInstance.startClientTeam(config.getTeamNumber());
        }
    }

    @Override
    public void close() {
        mInstance.close();
    }
}
