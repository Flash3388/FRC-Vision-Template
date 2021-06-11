package frc.team3388.vision.control;

public class Controls {

    private final NtControl mNtControl;
    private final CameraControl mCameraControl;
    private final CameraServerControl mServerControl;

    public Controls(NtControl ntControl, CameraControl cameraControl, CameraServerControl serverControl) {
        mNtControl = ntControl;
        mCameraControl = cameraControl;
        mServerControl = serverControl;
    }

    public NtControl getNtControl() {
        return mNtControl;
    }

    public CameraControl getCameraControl() {
        return mCameraControl;
    }

    public CameraServerControl getServerControl() {
        return mServerControl;
    }
}
