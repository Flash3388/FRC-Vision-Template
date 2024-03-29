package frc.team3388.vision.control;

import com.castle.util.closeables.Closeables;
import com.castle.util.closeables.Closer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSource;
import frc.team3388.vision.config.CameraConfig;
import frc.team3388.vision.config.Config;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CameraControl implements AutoCloseable {

    private final List<VideoSource> mCameras;

    public CameraControl(Config config, Logger logger) {
        mCameras = startCameras(config, logger);
    }

    public int getCameraCount() {
        return mCameras.size();
    }

    public VideoSource get(int index) {
        return mCameras.get(index);
    }

    @Override
    public void close() throws Exception {
        Closer closer = new Closer();
        closer.addAll(mCameras);
        closer.close();
    }

    private List<VideoSource> startCameras(Config config, Logger logger) {
        List<VideoSource> cameras = new ArrayList<>();
        try {
            for (CameraConfig cameraConfig : config.getCameraConfigs()) {
                cameras.add(startCamera(cameraConfig, logger));
            }

            return cameras;
        } catch (RuntimeException | Error e) {
            Closeables.silentClose(cameras);
            throw e;
        }
    }

    private VideoSource startCamera(CameraConfig config, Logger logger) {
        logger.info("Starting camera {} on {}", config.getName(), config.getPath());

        VideoSource camera = new UsbCamera(config.getName(), config.getPath());
        try {
            Gson gson = new GsonBuilder().create();
            camera.setConfigJson(gson.toJson(config.getJsonData()));

            return camera;
        } catch (RuntimeException | Error e) {
            Closeables.silentClose(camera);
            throw e;
        }
    }
}
