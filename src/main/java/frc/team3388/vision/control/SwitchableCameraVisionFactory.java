package frc.team3388.vision.control;

import com.beans.Property;
import com.beans.properties.atomic.AtomicProperty;
import com.flash3388.frc.nt.vision.NtVisionServer;
import com.flash3388.frc.nt.vision.StandardVisionOptions;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import frc.team3388.vision.ExtraVisionOptions;
import frc.team3388.vision.config.CameraConfig;
import frc.team3388.vision.config.Config;

import java.util.List;

public class SwitchableCameraVisionFactory implements VisionFactory {

    @Override
    public Vision create(Config config, List<VideoSource> cameras, NtControl ntControl) {
        NtVisionServer visionServer = new NtVisionServer("vision");
        MjpegServer cameraServer = initializeCameraServer(cameras);
        CvSink sink = new CvSink("vision-sink");
        Property<CameraConfig> cameraConfig = new AtomicProperty<>();

        initializeVisionOptions(visionServer, cameraServer, cameras, config.getCameraConfigs(), sink, cameraConfig);
        return new Vision(config, visionServer, cameraServer, new CameraSource(sink, cameraConfig, visionServer));
    }

    private MjpegServer initializeCameraServer(List<VideoSource> cameras) {
        MjpegServer cameraServer = CameraServer.getInstance().addSwitchedCamera("camera");
        cameraServer.setSource(cameras.get(0));

        return cameraServer;
    }

    private void initializeVisionOptions(NtVisionServer visionServer, MjpegServer cameraServer,
                                         List<VideoSource> cameras, List<CameraConfig> cameraConfigs,
                                         CvSink sink, Property<CameraConfig> configProperty) {
        visionServer.addOptionListener(StandardVisionOptions.EXPOSURE, (option, value)-> {
            cameraServer.getSourceProperty("exposure_absolute").set(value);
        });

        visionServer.addOptionListener(ExtraVisionOptions.SELECTED_CAMERA, (option, value)-> {
            if (value < 0 || value >= cameras.size()) {
                return;
            }

            VideoSource camera = cameras.get(value);
            CameraConfig config = cameraConfigs.get(value);

            sink.setSource(camera);
            cameraServer.setSource(camera);
            configProperty.set(config);

            int exposureValue = visionServer.getOptionOrDefault(StandardVisionOptions.EXPOSURE, 50);
            cameraServer.getSourceProperty("exposure_absolute").set(exposureValue);
        });
    }
}
