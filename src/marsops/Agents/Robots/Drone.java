package marsops.Agents.Robots;

import java.util.Set;
import marsops.AppEnums.*;

public class Drone extends Robot {

    public Drone(String id, String name, String location, int batteryLevel) {
        super(id, name, location, batteryLevel, Set.of(
            Capability.SCIENTIFIC_RESEARCH,
            Capability.AERIAL_RECON
        ));
    }

    @Override
    public Set<OperationType> getOperationTypes() {
        return Set.of(OperationType.REMOTE_DRONE_CONTROL);
    }
}
