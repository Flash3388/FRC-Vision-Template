package frc.team3388.vision.config;

import frc.team3388.vision.nt.NtMode;

public class NtConfig {

    private final NtMode mMode;
    private final String mIdentity;
    private final int mTeamNumber;
    private final String[] mAddresses;
    private final int mPort;

    public NtConfig(NtMode mode, String identity, int teamNumber, String[] addresses, int port) {
        mMode = mode;
        mIdentity = identity;
        mTeamNumber = teamNumber;
        mAddresses = addresses;
        mPort = port;
    }

    public NtMode getMode() {
        return mMode;
    }

    public String getIdentity() {return mIdentity;}

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
