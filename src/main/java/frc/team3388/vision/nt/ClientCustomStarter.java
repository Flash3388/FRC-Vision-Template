package frc.team3388.vision.nt;

import edu.wpi.first.networktables.NetworkTableInstance;
import frc.team3388.vision.config.NtConfig;
import org.slf4j.Logger;

import java.util.Arrays;

public class ClientCustomStarter implements NtStarter {

    @Override
    public void start(NetworkTableInstance ntInstance, NtConfig config, Logger logger) {
        logger.info("Setting up NetworkTables client for server {} at port {}",
                Arrays.toString(config.getAddresses()), config.getPort());
        ntInstance.setServer(config.getAddresses(), config.getPort());
        ntInstance.startClient3("CustomCameraClient");
    }
}
