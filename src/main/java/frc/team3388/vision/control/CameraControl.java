package frc.team3388.vision.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoSource;
import frc.team3388.vision.config.CameraConfig;
import frc.team3388.vision.config.Config;

import java.util.ArrayList;
import java.util.List;

public class CameraControl {

    private final Config mConfig;

    public CameraControl(Config config) {
        mConfig = config;
    }

    public List<VideoSource> startCameras() {
        List<VideoSource> cameras = new ArrayList<>();
        for (CameraConfig cameraConfig : mConfig.getCameraConfigs()) {
            cameras.add(startCamera(cameraConfig));
        }

        return cameras;
    }

    private VideoSource startCamera(CameraConfig config) {
        System.out.println(String.format("Starting camera %s on %s", config.getName(), config.getPath()));

        VideoSource camera = new UsbCamera(config.getName(), config.getPath());

        Gson gson = new GsonBuilder().create();
        camera.setConfigJson(gson.toJson(config.getJsonData()));

        return camera;
    }
}
