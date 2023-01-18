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

The configuration file allows editing different aspects of the code functionality. See [config.json](config.json) for example.

The configuration is made up of several parts in a hierarchy:
- root: all the settings
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
    - nt: settings about network table connection. Different modes require
        different settings.
      ```json
      "nt": {
          "mode": "mode for connection with network tables",
          "identity": "network identity to advertise client", || optional
          "team": "team number" || optional
          "addresses": [
              "address of the server of network tables",
              "another possibility of address"
          ] || optional,
          "port": "port of the server" || optional
      }
      ```
        - mode "CLIENT_TEAM"
            - connect as a client to the server
            - uses roboRIO addresses expected for the team
            - requires "team" setting
        - mode "CLIENT_CUSTOM"
            - connect as a client to the server
            - uses configured addresses and port to connect to the server
            - requires "addresses" setting
            - requires "port" setting
        - mode "SERVER"
            - run as the server for network tables
            - allows clients to connect
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
              "space": "color space to work with",
              "min": [
                  "minimum values to filter in the image for each color dimension"
              ],
              "max": [
                  "maximum values to filter in the image for each color dimension"
              ]    
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
        - color space: one of `RGB`, `HSV`, `BGR` 
      
## Vision Options

The supported `VisionOption` types are:
- `StandardVisionOptions.DEBUG`: additional debug information during the vision code
- `StandardVisionOptions.EXPOSURE`: exposure setting of the camera used for vision

In addition, there are custom vision options:
- `ExtraVisionOptions.SELECTED_CAMERA`: index for which camera to show. depending on the `VisionType` used this 
  might affect vision or simply affect what is shown by the MJpeg server
- `ExtraVisionOptions.COLOR_SPACE`: color space used in the color filtering. The `MIN`/`MAX` options
  available for use depend on the amount of dimensions in the space.
- `ExtraVisionOptions.COLOR_DIM1_MIN`: min value for the first dimension in the color filtering
- `ExtraVisionOptions.COLOR_DIM1_MAX`: max value for the first dimension in the color filtering
- `ExtraVisionOptions.COLOR_DIM2_MIN`: min value for the second dimension in the color filtering
- `ExtraVisionOptions.COLOR_DIM2_MAX`: max value for the second dimension in the color filtering
- `ExtraVisionOptions.COLOR_DIM3_MIN`: min value for the third dimension in the color filtering
- `ExtraVisionOptions.COLOR_DIM3_MAX`: max value for the third dimension in the color filtering

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

Deploying will push the compiled code and the [configuration file](config.json) to a place specified by gradle configuration, as seen
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

For security reasons, the password to connect to the deployment target is not saved in gradle.properties and needs to be
specified manually when deploying:
```shell script
./gradlew runRemote -PtargetPassword=somepassword
```

This will also deploy the code first, kill any previous processes
of the code running, and then start the new one.

The gradle task will output information from the standard output
of the remote process. Killing the gradle task (with CTRL+C for example)
will not kill the remote process. Run the task
```shell
./gradlew killRemote
```

#### Debug

For additional debug information, use `-PrunDebug=1`. This will
run the program with the options:
- `--log-level=DEBUG`
- `--console-log`

#### Run with Simulator

For testing, it is possible to run the code locally alongside a simulation of the robot.
The program will connect to locally-connected cameras following the configuration set for it.

In order for the code to be connected to network-tables to communicate with the robot,
the "nt" configuration should be set to connect to a locally running server:

```json
{
  "nt": {
    "mode": "CLIENT_CUSTOM",
    "addresses": [
      "127.0.0.1"
    ],
    "port": 1735
  }
}
```

In addition, network-tables has some native code. The code version must match the platform
on which it runs. The value is configured in [gradle properties file](gradle.properties) under the key `REMOTE_ARCH`. 
It needs to be modified to the local computer arch in order to run. 
The value should be `{osName}{arch}`, for example `linuxx86-64` for a computer running _linux_ with a _x86-64_ CPU.

To run locally, executed:
```shell
./gradlew run
```
