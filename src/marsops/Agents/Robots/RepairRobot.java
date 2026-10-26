package marsops.Agents.Robots;

import java.util.Set;
import marsops.AppEnums.*;

public class RepairRobot extends Robot {

    public RepairRobot(String id, String name, String location, int batteryLevel) {
        super(id, name, location, batteryLevel, Set.of(
            Capability.EQUIPMENT_REPAIR // REPAIR OFC 
        ));
    }
}
