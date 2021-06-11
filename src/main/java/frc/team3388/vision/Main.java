package frc.team3388.vision;

import com.castle.util.closeables.Closer;
import com.flash3388.flashlib.util.logging.LogLevel;
import com.flash3388.flashlib.util.logging.LoggerBuilder;
import frc.team3388.vision.config.Config;
import frc.team3388.vision.config.ConfigLoadException;
import frc.team3388.vision.config.ConfigLoader;
import frc.team3388.vision.control.CameraControl;
import frc.team3388.vision.control.CameraServerControl;
import frc.team3388.vision.control.Controls;
import frc.team3388.vision.control.NtControl;
import frc.team3388.vision.control.Vision;
import frc.team3388.vision.control.VisionFactory;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.slf4j.Logger;

import java.io.File;
import java.util.logging.SimpleFormatter;

import static net.sourceforge.argparse4j.impl.Arguments.booleanType;
import static net.sourceforge.argparse4j.impl.Arguments.enumStringType;
import static net.sourceforge.argparse4j.impl.Arguments.store;
import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;

public class Main {

    private static final String DEFAULT_CONFIG_FILE_NAME = "frc.json";

    public static void main(String[] args) throws ConfigLoadException, ArgumentParserException {
        ProgramOptions programOptions = handleArguments(args);

        Logger logger = makeLogger(programOptions);
        Closer closer = new Closer();

        logger.info("Loading configuration from {}", programOptions.getConfigFile());
        Config config = new ConfigLoader(programOptions.getConfigFile()).load();
        try {
            NtControl ntControl = new NtControl(config, logger);
            closer.add(ntControl);

            CameraControl cameraControl = new CameraControl(config, logger);
            closer.add(cameraControl);

            CameraServerControl cameraServerControl = new CameraServerControl(logger);
            closer.add(cameraServerControl);

            Controls controls = new Controls(ntControl, cameraControl, cameraServerControl);

            VisionFactory visionFactory = config.getVisionConfig().getVisionType().createFactory();
            Vision vision = visionFactory.create(config, controls);

            FrcVision frcVision = new FrcVision(controls, vision, config, logger,
                    (throwable)-> logger.error("Error", throwable));
            frcVision.run();
        } catch (Throwable t) {
            logger.error("Error in main", t);
        } finally {
            try {
                closer.close();
            } catch (Throwable t) {
                logger.error("Error from closer", t);
            }
        }
    }

    private static ProgramOptions handleArguments(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("frcvision")
                .build()
                .defaultHelp(true)
                .description("Vision Program for FRC");

        // config
        String userDir = System.getProperty("user.dir");
        parser.addArgument("-f", "--config-file")
                .required(false)
                .type(String.class)
                .action(store())
                .setDefault(userDir.concat("/").concat(DEFAULT_CONFIG_FILE_NAME))
                .help("Path to the configuration file of the program");

        // logging
        parser.addArgument("--console-log")
                .required(false)
                .type(booleanType())
                .action(storeTrue())
                .setDefault(false)
                .help("Enables log to the console");

        parser.addArgument("--file-log")
                .required(false)
                .type(booleanType())
                .action(storeTrue())
                .setDefault(false)
                .help("Enables log to the file");

        parser.addArgument("--log-file-out")
                .required(false)
                .type(String.class)
                .action(store())
                .setDefault("frcvision.log")
                .help("File to log into");

        parser.addArgument("--log-level")
                .required(false)
                .type(enumStringType(LogLevel.class))
                .action(store())
                .setDefault(LogLevel.ERROR)
                .help("Level of log output");

        Namespace namespace = parser.parseArgs(args);
        return new ProgramOptions(
                new File(namespace.getString("config-file")),
                namespace.getBoolean("console-log"),
                namespace.getBoolean("file-log"),
                new File(namespace.getString("log-file-out")),
                namespace.get("log-level"));
    }

    private static Logger makeLogger(ProgramOptions programOptions) {
        return new LoggerBuilder("frcvision")
                .enableConsoleLogging(programOptions.isConsoleLogEnabled())
                .enableFileLogging(programOptions.isFileLogEnabled())
                .setFilePattern(programOptions.getLogFile().getAbsolutePath())
                .setFileLogFormatter(new SimpleFormatter())
                .setLogLevel(programOptions.getLogLevel())
                .build();
    }
}
