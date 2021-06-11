package frc.team3388.vision;

import com.flash3388.flashlib.util.logging.LogLevel;

import java.io.File;

public class ProgramOptions {

    private final File mConfigFile;
    private final boolean mIsConsoleLogEnabled;
    private final boolean mIsFileLogEnabled;
    private final File mLogFile;
    private final LogLevel mLogLevel;

    public ProgramOptions(File configFile,
                          boolean isConsoleLogEnabled, boolean isFileLogEnabled,
                          File logFile, LogLevel logLevel) {
        mConfigFile = configFile;
        mIsConsoleLogEnabled = isConsoleLogEnabled;
        mIsFileLogEnabled = isFileLogEnabled;
        mLogFile = logFile;
        mLogLevel = logLevel;
    }

    public File getConfigFile() {
        return mConfigFile;
    }

    public boolean isConsoleLogEnabled() {
        return mIsConsoleLogEnabled;
    }

    public boolean isFileLogEnabled() {
        return mIsFileLogEnabled;
    }

    public File getLogFile() {
        return mLogFile;
    }

    public LogLevel getLogLevel() {
        return mLogLevel;
    }
}
