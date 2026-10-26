package marsops.Agents.Robots;

import java.util.Set;
import marsops.AppEnums.*;

public class ExplorationRover extends Robot {

    public ExplorationRover(String id, String name, String location, int batteryLevel) {
        super(id, name, location, batteryLevel, Set.of(
            Capability.SCIENTIFIC_RESEARCH //SCIENTIFIC 
        ));
    }
}
