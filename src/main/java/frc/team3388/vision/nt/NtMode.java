package frc.team3388.vision.nt;

import frc.team3388.vision.config.ConfigLoadException;
import frc.team3388.vision.config.NtConfig;

public enum NtMode {
    SERVER {
        @Override
        public void verifyConfig(NtConfig config) {

        }

        @Override
        public NtStarter createStarter() {
            return new ServerStarter();
        }
    },
    CLIENT_TEAM {
        @Override
        public void verifyConfig(NtConfig config) throws ConfigLoadException {
            if (config.getTeamNumber() <= 0) {
                throw new ConfigLoadException("team number required for specified nt mode");
            }
        }

        @Override
        public NtStarter createStarter() {
            return new ClientTeamStarter();
        }
    },
    CLIENT_CUSTOM {
        @Override
        public void verifyConfig(NtConfig config) throws ConfigLoadException {
            if (config.getAddresses() == null || config.getAddresses().length == 0) {
                throw new ConfigLoadException("addresses required for specified nt mode");
            }
            if (config.getPort() <= 0) {
                throw new ConfigLoadException("port required for specified nt mode");
            }
        }

        @Override
        public NtStarter createStarter() {
            return new ClientCustomStarter();
        }
    }
    ;

    public abstract void verifyConfig(NtConfig config) throws ConfigLoadException;
    public abstract NtStarter createStarter();
}
