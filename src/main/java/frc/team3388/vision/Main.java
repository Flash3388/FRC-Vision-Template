package frc.team3388.vision;

import com.castle.util.closeables.Closer;
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

import java.io.File;

import static net.sourceforge.argparse4j.impl.Arguments.store;
import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;

public class Main {

    private static final String DEFAULT_CONFIG_FILE_NAME = "frc.json";

    public static void main(String[] args) throws ConfigLoadException, ArgumentParserException {
        ProgramOptions programOptions = handleArguments(args);

        Config config = new ConfigLoader(programOptions.getConfigFile()).load();

        Closer closer = new Closer();
        try {
            NtControl ntControl = new NtControl(config);
            closer.add(ntControl);

            CameraControl cameraControl = new CameraControl(config);
            closer.add(cameraControl);

            CameraServerControl cameraServerControl = new CameraServerControl();
            closer.add(cameraServerControl);

            Controls controls = new Controls(ntControl, cameraControl, cameraServerControl);

            VisionFactory visionFactory = config.getVisionConfig().getVisionType().createFactory();
            Vision vision = visionFactory.create(config, controls);

            FrcVision frcVision = new FrcVision(controls, vision, config, Throwable::printStackTrace);
            frcVision.run();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                closer.close();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private static ProgramOptions handleArguments(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor("frcvision")
                .build()
                .defaultHelp(true)
                .description("Vision Program for FRC");

        String userDir = System.getProperty("user.dir");
        parser.addArgument("-c", "--config-file")
                .required(false)
                .type(String.class)
                .action(store())
                .setDefault(userDir.concat("/").concat(DEFAULT_CONFIG_FILE_NAME))
                .help("Path to the configuration file of the program");

        Namespace namespace = parser.parseArgs(args);
        return new ProgramOptions(
                new File(namespace.getString("config-file"))
        );
    }
}
