package frc.team3388.vision.control;

import edu.wpi.first.networktables.NetworkTableInstance;
import frc.team3388.vision.config.Config;
import frc.team3388.vision.config.NtMode;

public class NtControl {

    private final Config mConfig;
    private final NetworkTableInstance mNtInstance;

    public NtControl(Config config, NetworkTableInstance ntInstance) {
        mConfig = config;
        mNtInstance = ntInstance;
    }

    public void startNetworkTables() {
        if (mConfig.getNtMode() == NtMode.SERVER) {
            System.out.println("Setting up NetworkTables server");
            mNtInstance.startServer();
        } else {
            System.out.println("Setting up NetworkTables client for team " + mConfig.getTeamNumber());
            mNtInstance.startClientTeam(mConfig.getTeamNumber());
        }
    }
}
