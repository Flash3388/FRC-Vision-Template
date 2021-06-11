package frc.team3388.vision.control;

import com.flash3388.frc.nt.vision.NtVisionServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.team3388.vision.config.Config;
import frc.team3388.vision.config.NtMode;

public class NtControl implements AutoCloseable {

    private final NetworkTableInstance mInstance;
    private final NtVisionServer mVisionServer;

    public NtControl(Config config) {
        mInstance = NetworkTableInstance.getDefault();
        startNetworkTables(config);

        mVisionServer = new NtVisionServer(mInstance.getTable("vision"));
    }

    public NetworkTable getTable(String name) {
        return mInstance.getTable(name);
    }

    public NtVisionServer getVisionServer() {
        return mVisionServer;
    }

    private void startNetworkTables(Config config) {
        if (config.getNtMode() == NtMode.SERVER) {
            System.out.println("Setting up NetworkTables server");
            mInstance.startServer();
        } else {
            System.out.println("Setting up NetworkTables client for team " + config.getTeamNumber());
            mInstance.startClientTeam(config.getTeamNumber());
        }
    }

    @Override
    public void close() {
        mInstance.close();
    }
}
