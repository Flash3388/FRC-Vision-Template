package frc.team3388.vision.config;

import frc.team3388.vision.nt.NtMode;

public class NtConfig {

    private final NtMode mMode;
    private final int mTeamNumber;
    private final String[] mAddresses;
    private final int mPort;

    public NtConfig(NtMode mode, int teamNumber, String[] addresses, int port) {
        mMode = mode;
        mTeamNumber = teamNumber;
        mAddresses = addresses;
        mPort = port;
    }

    public NtMode getMode() {
        return mMode;
    }

    public int getTeamNumber() {
        return mTeamNumber;
    }

    public String[] getAddresses() {
        return mAddresses;
    }

    public int getPort() {
        return mPort;
    }
}
