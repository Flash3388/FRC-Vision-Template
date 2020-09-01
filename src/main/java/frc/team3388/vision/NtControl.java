package frc.team3388.vision;

import com.beans.IntProperty;
import com.flash3388.flashlib.vision.processing.color.ColorRange;
import com.flash3388.flashlib.vision.processing.color.HsvColorSettings;
import com.flash3388.frc.nt.beans.NtPropertyFactory;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import frc.team3388.vision.config.Config;
import frc.team3388.vision.config.NtMode;
import frc.team3388.vision.config.VisionConfig;

import java.util.List;

public class NtControl {

    private final Config mConfig;
    private final NetworkTableInstance mNtInstance;

    public NtControl(Config config) {
        mConfig = config;
        mNtInstance = NetworkTableInstance.getDefault();
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

    public void initializeCameraSwitching(List<VideoSource> cameras) {
        MjpegServer cameraServer = CameraServer.getInstance().addSwitchedCamera("camera");
        NetworkTable cameraControlTable = mNtInstance.getTable("cameraCtrl");
        NetworkTableEntry cameraSwitchingEntry = cameraControlTable.getEntry("camera");

        cameraServer.setSource(cameras.get(0));
        cameraSwitchingEntry.setDouble(0);
        cameraSwitchingEntry.addListener(notification -> {
            try {
                cameraServer.setSource(cameras.get((int) notification.value.getDouble()));
            } catch (IndexOutOfBoundsException ignored) {
            }
        }, EntryListenerFlags.kUpdate);
    }

    public void initializeExposureControl(VideoSource camera) {
        NetworkTable cameraControlTable = mNtInstance.getTable("cameraCtrl");
        NetworkTableEntry exposureEntry = cameraControlTable.getEntry("exposure");

        camera.getProperty("exposure_auto").set(1);
        exposureEntry.setDouble(camera.getProperty("exposure_absolute").get());

        exposureEntry.addListener((notification) -> {
            camera.getProperty("exposure_absolute").set((int) notification.value.getDouble());
        }, EntryListenerFlags.kUpdate);
    }

    public HsvColorSettings colorSettings() {
        VisionConfig visionConfig = mConfig.getVisionConfig();
        NetworkTable table = mNtInstance.getTable("colorSettings");

        IntProperty hueMin = NtPropertyFactory.newIntProperty(table, "hue.min");
        hueMin.setAsInt(visionConfig.getHue().start);
        IntProperty hueMax = NtPropertyFactory.newIntProperty(table, "hue.max");
        hueMax.setAsInt(visionConfig.getHue().end);
        IntProperty satMin = NtPropertyFactory.newIntProperty(table, "sat.min");
        hueMin.setAsInt(visionConfig.getSaturation().start);
        IntProperty satMax = NtPropertyFactory.newIntProperty(table, "sat.max");
        hueMin.setAsInt(visionConfig.getSaturation().end);
        IntProperty valMin = NtPropertyFactory.newIntProperty(table, "val.min");
        hueMin.setAsInt(visionConfig.getValue().start);
        IntProperty valMax = NtPropertyFactory.newIntProperty(table, "val.max");
        hueMin.setAsInt(visionConfig.getValue().end);

        return new HsvColorSettings(
                new ColorRange(hueMin, hueMax),
                new ColorRange(satMin, satMax),
                new ColorRange(valMin, valMax)
        );
    }

    public NetworkTable getResultsTable() {
        return mNtInstance.getTable("visionResults");
    }
}
