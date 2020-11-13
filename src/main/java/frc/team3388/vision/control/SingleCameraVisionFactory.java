package frc.team3388.vision.control;

import com.flash3388.frc.nt.vision.NtVisionServer;
import com.flash3388.frc.nt.vision.StandardVisionOptions;
import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.VideoSource;
import edu.wpi.first.cameraserver.CameraServer;
import frc.team3388.vision.ExtraVisionOptions;
import frc.team3388.vision.config.CameraConfig;
import frc.team3388.vision.config.Config;

import java.util.List;

public class SingleCameraVisionFactory implements VisionFactory {


    @Override
    public Vision create(Config config, List<VideoSource> cameras, NtControl ntControl) {
        NtVisionServer visionServer = new NtVisionServer("vision");
        MjpegServer cameraServer = initializeCameraServer(cameras);

        int cameraIndex = config.getVisionConfig().getExtras().get("camera").getAsInt();
        VideoSource camera = cameras.get(cameraIndex);
        CameraConfig cameraConfig = config.getCameraConfigs().get(cameraIndex);

        initializeVisionOptions(visionServer, cameraServer, cameras, camera);
        return new Vision(config, visionServer, cameraServer, new CameraSource(camera, cameraConfig, visionServer));
    }

    private MjpegServer initializeCameraServer(List<VideoSource> cameras) {
        MjpegServer cameraServer = CameraServer.getInstance().addSwitchedCamera("camera");
        cameraServer.setSource(cameras.get(0));

        return cameraServer;
    }

    private void initializeVisionOptions(NtVisionServer visionServer, MjpegServer cameraServer,
                                         List<VideoSource> cameras, VideoSource camera) {
        visionServer.addOptionListener(StandardVisionOptions.EXPOSURE, (option, value)-> {
            camera.getProperty("exposure_absolute").set(value);
        });

        visionServer.addOptionListener(ExtraVisionOptions.SELECTED_CAMERA, (option, value)-> {
            if (value < 0 || value >= cameras.size()) {
                return;
            }

            cameraServer.setSource(cameras.get(value));
        });
    }
}
