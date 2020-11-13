package frc.team3388.vision;

import com.beans.IntProperty;
import com.flash3388.flashlib.vision.processing.color.ColorRange;
import com.flash3388.flashlib.vision.processing.color.HsvColorSettings;
import com.flash3388.frc.nt.beans.NtProperties;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.team3388.vision.config.ColorConfig;
import frc.team3388.vision.config.Config;
import frc.team3388.vision.config.NtMode;
import frc.team3388.vision.config.VisionConfig;

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

    public HsvColorSettings configureColorSettings(NetworkTable table) {
        ColorConfig colorConfig = mConfig.getVisionConfig().getColorConfig();

        IntProperty hueMin = NtProperties.newIntProperty(table, "hue.min");
        hueMin.setAsInt(colorConfig.getHue().start);
        IntProperty hueMax = NtProperties.newIntProperty(table, "hue.max");
        hueMax.setAsInt(colorConfig.getHue().end);
        IntProperty satMin = NtProperties.newIntProperty(table, "sat.min");
        hueMin.setAsInt(colorConfig.getSaturation().start);
        IntProperty satMax = NtProperties.newIntProperty(table, "sat.max");
        hueMin.setAsInt(colorConfig.getSaturation().end);
        IntProperty valMin = NtProperties.newIntProperty(table, "val.min");
        hueMin.setAsInt(colorConfig.getValue().start);
        IntProperty valMax = NtProperties.newIntProperty(table, "val.max");
        hueMin.setAsInt(colorConfig.getValue().end);

        return new HsvColorSettings(
                new ColorRange(hueMin, hueMax),
                new ColorRange(satMin, satMax),
                new ColorRange(valMin, valMax)
        );
    }
}
