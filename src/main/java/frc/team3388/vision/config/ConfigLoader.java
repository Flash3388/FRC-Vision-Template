package frc.team3388.vision.config;

import com.castle.reflect.Types;
import com.flash3388.flashlib.vision.control.VisionOption;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.opencv.core.Range;

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

    public ConfigLoader(File configFile) {
        mConfigFile = configFile;
        mKnownVisionOptions = new KnownVisionOptions();
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
        int teamNumber = parseTeamNumber(rootObject);
        NtMode ntMode = parseNtMode(rootObject);
        List<CameraConfig> cameraConfigs = parseCameraConfigs(rootObject);
        VisionConfig visionConfig = parseVisionConfig(rootObject);
        TargetConfig targetConfig = parseTargetConfig(rootObject);

        return new Config(teamNumber, ntMode, cameraConfigs, visionConfig, targetConfig);
    }

    private int parseTeamNumber(JsonObject rootObject) throws ConfigLoadException {
        try {
            if (!rootObject.has("team")) {
                throw new ConfigLoadException("missing `team` element");
            }

            return rootObject.get("team").getAsInt();
        } catch (ClassCastException e) {
            throw new ConfigLoadException("`team` element is not an int");
        }
    }

    private NtMode parseNtMode(JsonObject rootObject) throws ConfigLoadException {
        try {
            if (!rootObject.has("ntmode")) {
                return NtMode.UNDEFINED;
            }

            String isServerStr = rootObject.get("ntmode").getAsString();

            if (isServerStr.equals("server")) {
                return NtMode.SERVER;
            }
            if (isServerStr.equals("client")) {
                return NtMode.CLIENT;
            }

            throw new ConfigLoadException("`ntmode` contains invalid value: " + isServerStr);
        } catch (ClassCastException e) {
            throw new ConfigLoadException("`ntmode` element is not a string");
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
        Range hue = parseRange(root, "hue");
        Range saturation = parseRange(root, "saturation");
        Range value = parseRange(root, "value");

        return new ColorConfig(hue, saturation, value);
    }

    private Range parseRange(JsonObject rootObject, String memberName) throws ConfigLoadException {
        try {
            if (!rootObject.has(memberName)) {
                throw new ConfigLoadException("Missing " + memberName);
            }

            JsonObject range = rootObject.getAsJsonObject(memberName);
            if (!range.has("min") || !range.has("max")) {
                throw new ConfigLoadException("Range missing min/max: " + memberName);
            }

            return new Range(
                    range.get("min").getAsInt(),
                    range.get("max").getAsInt()
            );
        } catch (ClassCastException e) {
            throw new ConfigLoadException("Range type error, should be object with min/max integers: " + memberName);
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
        } else if (element.isNumber() && type.getSuperclass().equals(Number.class)) {
            return Types.smartCast(element.getAsNumber(), type);
        } else if (element.isString() && type.equals(String.class)) {
            return element.getAsString();
        }

        throw new ConfigLoadException("Can't parse " + element + " to " + type);
    }
}
