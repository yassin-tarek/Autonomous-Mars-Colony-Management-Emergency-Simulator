package marsops.missions;

import marsops.colony.Colony;
import marsops.colony.MarsEnvironment;
import marsops.colony.ResourceManager;

/**
 * Alias class for SimulationManager as referenced in project design documentation.
 */
public class Simulation extends SimulationManager {

    public Simulation(Colony colony, MarsEnvironment environment, ResourceManager resourceManager) {
        super(colony, environment, resourceManager);
    }

    public Simulation(Colony colony) {
        super(colony);
    }

    public Simulation(MarsEnvironment environment, ResourceManager resourceManager) {
        super(environment, resourceManager);
    }
}
