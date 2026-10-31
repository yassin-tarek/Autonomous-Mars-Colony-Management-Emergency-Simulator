package marsops.missions;

import marsops.Agents.Agent;
import marsops.AppEnums.Capability;
import marsops.AppEnums.OperationType;
import marsops.AppEnums.ResourceType;

/**
 * Search and rescue operation to recover stranded entities.
 * Requires communication capabilities, fuel, and battery power.
 * Blocked during COMMUNICATION_DISRUPTION.
 */
public class RescueMission extends Mission {
    private final String rescueLocation;

    public RescueMission(String id, String name, int duration, String rescueLocation) {
        super(id, name, MissionType.RESCUE, duration);
        this.rescueLocation = (rescueLocation != null && !rescueLocation.trim().isEmpty())
                ? rescueLocation : "Martian Surface - Sector Unknown";

        // Required capabilities
        addRequiredCapability(Capability.COMMUNICATION);

        // Required resources
        addRequiredResource(ResourceType.FUEL, 10);
        addRequiredResource(ResourceType.BATTERY_POWER, 5);

        // Communication dependent operation
        setOperationType(OperationType.COMMUNICATION_DEPENDENT);
    }

    public RescueMission(String id, String name, int duration) {
        this(id, name, duration, "Martian Surface Crater");
    }

    public String getRescueLocation() {
        return rescueLocation;
    }

    @Override
    public boolean canBePerformedBy(Agent agent) {
        if (agent == null || !agent.isReady()) {
            return false;
        }
        return agent.hasCapability(Capability.COMMUNICATION) || agent.hasCapability(Capability.AERIAL_RECON);
    }
}
