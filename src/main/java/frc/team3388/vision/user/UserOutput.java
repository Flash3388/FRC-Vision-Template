package frc.team3388.vision.user;

import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;

import java.util.Map;
import java.util.function.Consumer;

public class UserOutput implements Consumer<Analysis> {

    private final NetworkTable mResultsTable;

    public UserOutput(NetworkTable resultsTable) {
        mResultsTable = resultsTable;
    }

    @Override
    public void accept(Analysis analysis) {
        for (Map.Entry<String, Object> entry : analysis.getData().entrySet()) {
            NetworkTableEntry tableEntry = mResultsTable.getEntry(entry.getKey());
            tableEntry.setValue(entry.getValue());
        }
    }
}
