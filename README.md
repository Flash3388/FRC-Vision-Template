# FRC Vision

Vision code for FRC which runs separately from the robot code. The code will connect to network tables and allow
running and configuring vision code based on the `VisionControl` interface from FlashLib.

## Program Parameters

- `--config-file=PATH`: sets the path to the configuration file. Default `/home/flash/frc.json`.
- `--console-log`: enables logging to the console
- `--file-log`: enables logging to file
- `--log-file-out=PATH`: sets the log file path. Default `frcvision.log`.
- `--log-level={DEBUG|INFO|WARN|ERROR}`: sets the log level. Default `ERROR`.

## Configuration

The configuration file allows editing different aspects of the code functionality. See [frc.json](frc.json) for example.

The configuration is made up of several parts in a hierarchy:
- root: all the settings
    - team: the team number
    - ntmode: configuration of the network tables run mode
    - cameras: configurations for the connected cameras, should have information about all the cameras in a format matching
      the following template:
      ```JSON
      "cameras": [
          {
            "name": "a name for the camera. it's purely for debugging reasons",
            "path": "device path, like /dev/video0",
            "pixel format": "MJPEG, YUYV, etc || optional",
            "width": "width of the camera resolution || optional",
            "height": "height of the camera resolution  || optional",
            "fps": "frames per second  || optional",
            "brightness": "brightness precentage || optional",
            "white balance": "auto, hold, specific value, etc || optional",
            "exposure": "auto, hold, specific value, etc  || optional",
            "fov": "field of view in radians || optional"
            "properties": [
                {
                  "name": "custom prop name",
                  "value": "custom prop value"
                }
            ]
          }
      ]
      ```
    - target: information about the target to find matching the following format:
      ```JSON
      "target": {
          "realWidth": "real life width in cm",
          "dimensionsRatio": "ratio between real life width and height"
      }
      ```
    - vision: configuration for the vision code running matching the following format:
      ```JSON
      "vision": {
          "color": {
              "hue": {
                  "min": "initial min hue value (0->180)",
                  "max": "initial max hue value (0->180)",
              },
              "saturation": {
                  "min": "initial min saturation value (0->255)",
                  "max": "initial max saturation value (0->255)",
              },
              "value": {
                  "min": "initial min value value (0->255)",
                  "max": "initial max value value (0->255)",
              }
          },
          "type": "type of vision control, which determine how to use the cameras. Based on VisionType enum",
          "camera": "only for SINGLE_CAMERA type - specifies index of camera to use from among the camera configs"
          "autoStart": "boolean indicating whether to start vision immediately, or wait for a start command"
          "options": {
              "option_name": "value for option"
          } 
      }
      ```
        - `VisionType` indicates how the vision should reflect when having several cameras.
            - `SINGLE_CAMERA`: vision code will only run on one camera while the rest will simply be used for looking.
            - `SWITCHED_CAMERA`: vision code will use the currently selected camera. Switching cameras will affect vision as well.
      
## Vision Options

The supported `VisionOption` types are:
- `StandardVisionOptions.DEBUG`: additional debug information during the vision code
- `StandardVisionOptions.EXPOSURE`: exposure setting of the camera used for vision

In addition, there are custom vision options:
- `ExtraVisionOptions.SELECTED_CAMERA`: index for which camera to show. depending on the `VisionType` used this 
  might affect vision or simply affect what is shown by the MJpeg server
- `ExtraVisionOptions.HUE_MIN`: min hue value for the color filtering
- `ExtraVisionOptions.HUE_MAX`: max hue value for the color filtering
- `ExtraVisionOptions.SATURATION_MIN`: min saturation value for the color filtering
- `ExtraVisionOptions.SATURATION_MAX`: max saturation value for the color filtering
- `ExtraVisionOptions.VALUE_MIN`: min value value for the color filtering
- `ExtraVisionOptions.VALUE_MAX`: max value value for the color filtering

## Robot Interaction

To interact with this vision code from the robot, use `NtRemoteVisionControl` from `flashlib.frc.nt`
which implements remote `VisionControl` through network-tables.
```Java
VisionControl visionControl = new NtRemoteVisionControl("vision");
visionControl.start();

visionControl.setOption(StandardVisionOption.DEBUG, true);
Optional<VisionResult> result = visionControl.getLatestResult(true);
result.ifPresent((value)-> System.out.println(value.getAnalysis()));
```

See [this example](https://github.com/Flash3388/FlashFRC/tree/development/examples/vision/robot-nt-vision) for more.

## Build and Deploy

Building and deploying is based on _gradle_ tasks. Run the wrapper (`./gradlew` on UNIX and `gradlew.bat` on Windows)
with the tasks `build` to build and `deploy` to deploy.

Deploying will push the compiled code and the [configuration file](frc.json) to a place specified by gradle configuration, as seen
in [gradle.properties](gradle.properties):
- `DEPLOY_PATH`: absolute path on the remote to deploy the code. Should exist. Will be the parent folder of the code.
- `DEPLOY_HOST`: hostname of the remote device to deploy to.
- `DEPLOY_USER`: username to connect to on the remote when deploying.

For security reasons, the password to connect to the deploy target is not saved in gradle.properties and needs to be
specified manually when deploying:
```shell script
./gradlew deploy -PtargetPassword=somepassword
```

The program will be deployed to `DEPLOY_PATH/frcvision.zip` and then
extracted to `DEPLOY_PATH/frcvision`.

### Running

To run the deployed code, run the `DEPLOY_PATH/frcvision/bin/frcvision` file.
Or, run the _gradle_ task `runRemote`.

For security reasons, the password to connect to the deploy target is not saved in gradle.properties and needs to be
specified manually when deploying:
```shell script
./gradlew runRemote -PtargetPassword=somepassword
```

This will also deploy the code first, kill any previous processes
of the code running, and then start the new one.

The gradle task will output information from the standard output
of the remote process. Killing the gradle task (with CTRL+C for example)
will not kill the remote process.

#### Debug

For additional debug information, use `-PrunDebug=1`. This will
run the program with the options:
- `--log-level=DEBUG`
- `--console-log`
