package frc.team3388.vision.control;

import com.flash3388.frc.nt.vision.NtVisionServer;
import com.flash3388.frc.nt.vision.StandardVisionOptions;
import edu.wpi.first.cscore.VideoSource;
import frc.team3388.vision.config.CameraConfig;
import frc.team3388.vision.config.Config;
import frc.team3388.vision.config.VisionConfig;

public class SingleCameraVisionFactory implements VisionFactory {

    @Override
    public Vision create(Config config, Controls controls) {
        int cameraIndex = config.getVisionConfig().getExtras().get("camera").getAsInt();
        VideoSource camera = controls.getCameraControl().get(cameraIndex);
        CameraConfig cameraConfig = config.getCameraConfigs().get(cameraIndex);

        controls.getServerControl().setMainCameraSource(camera);

        initializeVisionOptions(
                controls.getNtControl().getVisionServer(),
                camera,
                config.getVisionConfig());

        return new Vision(
                new CameraSource(camera, cameraConfig, controls.getNtControl().getVisionServer()),
                controls.getNtControl().getVisionServer(),
                config
        );
    }

    private void initializeVisionOptions(NtVisionServer visionServer, VideoSource camera,
                                         VisionConfig config) {
        visionServer.addOptionListener(StandardVisionOptions.EXPOSURE, (option, value)-> {
            camera.getProperty("exposure_absolute").set(value);
        });

        config.getOptionsConfig().loadInto(visionServer);
    }
}
