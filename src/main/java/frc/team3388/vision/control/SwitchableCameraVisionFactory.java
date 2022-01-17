package frc.team3388.vision.control;

import com.beans.Property;
import com.beans.properties.atomic.AtomicProperty;
import com.flash3388.frc.nt.vision.NtVisionServer;
import com.flash3388.frc.nt.vision.StandardVisionOptions;
import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.VideoSource;
import frc.team3388.vision.ExtraVisionOptions;
import frc.team3388.vision.config.CameraConfig;
import frc.team3388.vision.config.Config;
import frc.team3388.vision.config.VisionConfig;

import java.util.List;

public class SwitchableCameraVisionFactory implements VisionFactory {

    @Override
    public Vision create(Config config, Controls controls) {
        CvSink sink = new CvSink("vision-sink");
        Property<CameraConfig> cameraConfigProperty = new AtomicProperty<>();

        int cameraIndex = config.getVisionConfig().getExtras().get("camera").getAsInt();
        VideoSource camera = controls.getCameraControl().get(cameraIndex);
        sink.setSource(camera);
        controls.getServerControl().setMainCameraSource(camera);

        initializeVisionOptions(
                controls.getNtControl().getVisionServer(),
                controls.getCameraControl(), controls.getServerControl(),
                sink, cameraConfigProperty, config.getCameraConfigs(),
                config.getVisionConfig());

        return new Vision(
                new CameraSource(sink, cameraConfigProperty, controls.getNtControl().getVisionServer()),
                controls.getNtControl().getVisionServer(),
                config
        );
    }

    private void initializeVisionOptions(NtVisionServer visionServer, CameraControl cameraControl,
                                         CameraServerControl cameraServerControl,
                                         CvSink sink, Property<CameraConfig> cameraConfigProperty,
                                         List<CameraConfig> cameraConfigs, VisionConfig config) {
        visionServer.addOptionListener(StandardVisionOptions.EXPOSURE, (option, value)-> {
            int selectedCamera = visionServer.getOptionOrDefault(ExtraVisionOptions.SELECTED_CAMERA, 0);
            VideoSource camera = cameraControl.get(selectedCamera);
            camera.getProperty("exposure_absolute").set(value);
        });

        visionServer.addOptionListener(ExtraVisionOptions.SELECTED_CAMERA, (option, value)-> {
            if (value < 0 || value >= cameraControl.getCameraCount()) {
                return;
            }

            VideoSource camera = cameraControl.get(value);
            CameraConfig cameraConfig = cameraConfigs.get(value);

            sink.setSource(camera);
            cameraServerControl.setMainCameraSource(camera);
            cameraConfigProperty.set(cameraConfig);

            int exposureValue = visionServer.getOptionOrDefault(StandardVisionOptions.EXPOSURE, 50);
            camera.getProperty("exposure_absolute").set(exposureValue);
        });

        config.getOptionsConfig().loadInto(visionServer);
    }
}
