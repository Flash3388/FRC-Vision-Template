package frc.team3388.vision.nt;

import edu.wpi.first.networktables.NetworkTableInstance;
import frc.team3388.vision.config.NtConfig;
import org.slf4j.Logger;

public class ServerStarter implements NtStarter {

    @Override
    public void start(NetworkTableInstance ntInstance, NtConfig config, Logger logger) {
        logger.info("Setting up NetworkTables server");
        ntInstance.startServer();
    }
}
