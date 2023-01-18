package frc.team3388.vision.config;

import com.castle.reflect.NumberAdapter;
import com.castle.reflect.Types;
import com.flash3388.flashlib.vision.control.VisionOption;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import frc.team3388.vision.color.ColorSpace;
import frc.team3388.vision.nt.NtMode;
import org.opencv.core.Scalar;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigLoader {

    private final File mConfigFile;
    private final KnownVisionOptions mKnownVisionOptions;
    private NumberAdapter mNumberAdapter;

    public ConfigLoader(File configFile) {
        mConfigFile = configFile;
        mKnownVisionOptions = new KnownVisionOptions();
        mNumberAdapter = new NumberAdapter();
    }

    public Config load() throws ConfigLoadException {
        try {
            JsonElement root = readRootElement();
            if (!root.isJsonObject()) {
                throw new ConfigLoadException("root element is not a json object");
            }

            JsonObject rootObject = root.getAsJsonObject();
            return parseRootObject(rootObject);
        } catch (IOException e) {
            throw new ConfigLoadException(e);
        }
    }

    private JsonElement readRootElement() throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(mConfigFile.toPath())) {
            return new JsonParser().parse(reader);
        }
    }

    private Config parseRootObject(JsonObject rootObject) throws ConfigLoadException {
        List<CameraConfig> cameraConfigs = parseCameraConfigs(rootObject);
        NtConfig ntConfig = parseNtConfig(rootObject);
        VisionConfig visionConfig = parseVisionConfig(rootObject);
        TargetConfig targetConfig = parseTargetConfig(rootObject);

        return new Config(cameraConfigs, ntConfig, visionConfig, targetConfig);
    }

    private NtConfig parseNtConfig(JsonObject rootObject) throws ConfigLoadException {
        try {
            if (!rootObject.has("nt")) {
                throw new ConfigLoadException("missing `nt` element");
            }

            JsonObject nt = rootObject.get("nt").getAsJsonObject();

            NtMode mode = NtMode.valueOf(nt.get("mode").getAsString());

            int teamNumber = -1;
            if (nt.has("team")) {
                teamNumber = nt.get("team").getAsInt();
            }

            String[] addresses = null;
            if (nt.has("addresses")) {
                JsonArray jsonAddresses = nt.get("addresses").getAsJsonArray();
                addresses = new String[jsonAddresses.size()];
                for (int i = 0; i < jsonAddresses.size(); i++) {
                    addresses[i] = jsonAddresses.get(i).getAsString();
                }
            }

            int port = -1;
            if (nt.has("port")) {
                port = nt.get("port").getAsInt();
            }

            NtConfig config = new NtConfig(mode, teamNumber, addresses, port);
            mode.verifyConfig(config);

            return config;
        } catch (ClassCastException | EnumConstantNotPresentException e) {
            throw new ConfigLoadException("`nt` config is misconfigured");
        }
    }

    private List<CameraConfig> parseCameraConfigs(JsonObject rootObject) throws ConfigLoadException {
        if (!rootObject.has("cameras")) {
            throw new ConfigLoadException("missing `cameras` element");
        }

        JsonElement camerasElement = rootObject.get("cameras");
        if (!camerasElement.isJsonArray()) {
            throw new ConfigLoadException("`cameras` is not an array");
        }

        JsonArray cameras = camerasElement.getAsJsonArray();
        List<CameraConfig> cameraConfigs = new ArrayList<>();

        for (JsonElement cameraElement : cameras) {
            if (!cameraElement.isJsonObject()) {
                throw new ConfigLoadException("element in `cameras` is not an object");
            }

            JsonObject cameraObject = cameraElement.getAsJsonObject();
            cameraConfigs.add(parseCameraConfig(cameraObject));
        }

        return cameraConfigs;
    }

    private CameraConfig parseCameraConfig(JsonObject cameraRoot) throws ConfigLoadException {
        try {
            if (!cameraRoot.has("name")) {
                throw new ConfigLoadException("camera element missing `name`");
            }
            if (!cameraRoot.has("path")) {
                throw new ConfigLoadException("camera element missing `path`");
            }
            if (!cameraRoot.has("fov")) {
                throw new ConfigLoadException("camera element missing `fov`");
            }

            String name = cameraRoot.get("name").getAsString();
            String path = cameraRoot.get("path").getAsString();
            double fov = cameraRoot.get("fov").getAsDouble();

            return new CameraConfig(name, path, cameraRoot, fov);
        } catch (ClassCastException | IllegalStateException e) {
            throw new ConfigLoadException("camera frc.team3388.vision.config element is not of wanted type", e);
        }
    }

    private VisionConfig parseVisionConfig(JsonObject rootObject) throws ConfigLoadException {
        if (!rootObject.has("vision")) {
            throw new ConfigLoadException("missing 'vision'");
        }

        JsonObject visionRoot = rootObject.getAsJsonObject("vision");
        ColorConfig colorConfig = parseColorConfig(visionRoot);

        if (!visionRoot.has("type")) {
            throw new ConfigLoadException("missing 'type' for 'vision'");
        }
        String type = visionRoot.get("type").getAsString();
        VisionType visionType;
        try {
            visionType = VisionType.valueOf(type);
        } catch (IllegalArgumentException e) {
            throw new ConfigLoadException("unknown value for 'type' in 'vision'. supported: " + Arrays.asList(VisionType.values()));
        }

        boolean autoStart = false;
        if (visionRoot.has("autoStart")) {
            autoStart = visionRoot.get("autoStart").getAsBoolean();
        }

        VisionOptionsConfig optionsConfig = parseVisionOptions(visionRoot);

        return new VisionConfig(colorConfig, visionType, autoStart, optionsConfig, visionRoot);
    }

    private ColorConfig parseColorConfig(JsonObject rootObject) throws ConfigLoadException {
        if (!rootObject.has("color")) {
            throw new ConfigLoadException("missing 'color'");
        }

        JsonObject root = rootObject.getAsJsonObject("color");

        ColorSpace colorSpace = ColorSpace.HSV;
        if (root.has("space")) {
            colorSpace = ColorSpace.valueOf(root.get("space").getAsString());
        }

        Scalar min = parseScalar(root, "min");
        Scalar max = parseScalar(root, "max");

        return new ColorConfig(colorSpace, min, max);
    }

    private Scalar parseScalar(JsonObject rootObject, String memberName) throws ConfigLoadException {
        try {
            if (!rootObject.has(memberName)) {
                throw new ConfigLoadException("Missing " + memberName);
            }

            JsonArray scalar = rootObject.getAsJsonArray(memberName);

            double[] values = {0, 0, 0, 0};
            for (int i = 0; i < scalar.size(); i++) {
                values[i] = scalar.get(i).getAsInt();
            }

            return new Scalar(values);
        } catch (ClassCastException e) {
            throw new ConfigLoadException("Scalar type error, should be object with min/max integers: " + memberName);
        }
    }

    private TargetConfig parseTargetConfig(JsonObject rootObject) throws ConfigLoadException {
        if (!rootObject.has("target")) {
            throw new ConfigLoadException("missing 'target'");
        }

        return new TargetConfig(rootObject.getAsJsonObject("target"));
    }

    private VisionOptionsConfig parseVisionOptions(JsonObject rootObject) throws ConfigLoadException {
        if (!rootObject.has("options")) {
            return new VisionOptionsConfig();
        }

        rootObject = rootObject.getAsJsonObject("options");

        Map<String, VisionOptionConfig<?>> options = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : rootObject.entrySet()) {
            options.put(entry.getKey(),
                    parseVisionOption(entry.getValue(), entry.getKey()));
        }

        return new VisionOptionsConfig(options);
    }

    private VisionOptionConfig<?> parseVisionOption(JsonElement element, String name) throws ConfigLoadException {
        VisionOption<?> option = mKnownVisionOptions.find(name);
        if (!element.isJsonPrimitive()) {
            throw new ConfigLoadException("expected primitive");
        }

        Object value = parseByType(element.getAsJsonPrimitive(), option.valueType());
        return new VisionOptionConfig(option, value);
    }

    private Object parseByType(JsonPrimitive element, Class<?> type) throws ConfigLoadException {
        if (element.isBoolean() && type.equals(Boolean.class)) {
            return element.getAsBoolean();
        } else if (element.isNumber() && type.getSuperclass().equals(Number.class) && mNumberAdapter.canAdapt(type)) {
            return mNumberAdapter.adapt(element.getAsNumber(), type);
        } else if (element.isString() && type.equals(String.class)) {
            return element.getAsString();
        }

        throw new ConfigLoadException("Can't parse " + element + " to " + type);
    }
}
