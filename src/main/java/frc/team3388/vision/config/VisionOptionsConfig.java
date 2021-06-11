package frc.team3388.vision.config;

import com.flash3388.flashlib.vision.control.VisionOption;
import com.flash3388.flashlib.vision.control.VisionOptions;
import com.flash3388.frc.nt.vision.NtVisionServer;

import java.util.HashMap;
import java.util.Map;

public class VisionOptionsConfig {

    private final Map<String, VisionOptionConfig<?>> mOptions;

    public VisionOptionsConfig(Map<String, VisionOptionConfig<?>> options) {
        mOptions = options;
    }

    public VisionOptionsConfig() {
        this(new HashMap<>());
    }

    public void loadInto(NtVisionServer options) {
        for (Map.Entry<String, VisionOptionConfig<?>> entry :
            mOptions.entrySet()){
            options.setOption(
                    (VisionOption) entry.getValue().getOption(),
                    entry.getValue().getValue());
        }
    }
}
