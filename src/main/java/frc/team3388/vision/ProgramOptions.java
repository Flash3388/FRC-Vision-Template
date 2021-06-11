package frc.team3388.vision;

import java.io.File;

public class ProgramOptions {

    private final File mConfigFile;

    public ProgramOptions(File configFile) {
        mConfigFile = configFile;
    }

    public File getConfigFile() {
        return mConfigFile;
    }
}
