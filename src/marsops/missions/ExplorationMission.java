package marsops.missions;

import marsops.Agents.Agent;
import marsops.AppEnums.Capability;
import marsops.AppEnums.OperationType;
import marsops.AppEnums.ResourceType;

/**
 * Surface exploration mission across Martian terrain.
 * Requires vehicle exploration capabilities, scientific skills, battery power, and fuel.
 * Restricted during DUST_STORM environmental conditions.
 */
public class ExplorationMission extends Mission {
    private final String targetRegion;

    public ExplorationMission(String id, String name, int duration, String targetRegion) {
        super(id, name, MissionType.EXPLORATION, duration);
        this.targetRegion = (targetRegion != null && !targetRegion.trim().isEmpty())
                ? targetRegion : "Uncharted Surface Sector";

        // Required capabilities
        addRequiredCapability(Capability.SCIENTIFIC_RESEARCH);
        addRequiredCapability(Capability.SURFACE_EXPLORATION);

        // Required resources
        addRequiredResource(ResourceType.BATTERY_POWER, 10);
        addRequiredResource(ResourceType.FUEL, 5);

        // Surface operation marker for environmental restriction checks
        setOperationType(OperationType.SURFACE_OPERATION);
    }

    public ExplorationMission(String id, String name, int duration) {
        this(id, name, duration, "Valles Marineris");
    }

    public String getTargetRegion() {
        return targetRegion;
    }

    @Override
    public boolean canBePerformedBy(Agent agent) {
        if (agent == null || !agent.isReady()) {
            return false;
        }
        // An agent qualifies if it possesses scientific skill or surface traversal capability
        return agent.hasCapability(Capability.SCIENTIFIC_RESEARCH) ||
               agent.hasCapability(Capability.SURFACE_EXPLORATION);
    }
}
