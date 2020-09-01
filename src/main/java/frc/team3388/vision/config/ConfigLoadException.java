package frc.team3388.vision.config;

public class ConfigLoadException extends Exception {

    public ConfigLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigLoadException(Throwable cause) {
        super(cause);
    }

    public ConfigLoadException(String message) {
        super(message);
    }
}
