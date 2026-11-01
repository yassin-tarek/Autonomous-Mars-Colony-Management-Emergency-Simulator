package marsops.missions;

import marsops.Agents.Agent;
import marsops.Agents.Robots.ExplorationRover;
import marsops.AppEnums.Capability;
import marsops.AppEnums.MissionType;
import marsops.AppEnums.OperationType;
import marsops.AppEnums.ResourceType;

/**
 * Search and rescue operation to recover a stranded rover.
 * On successful completion the rover is repaired and restored to OPERATIONAL.
 * Requires communication capabilities, fuel, battery power and spare parts.
 * Blocked during COMMUNICATION_DISRUPTION.
 */
public class RescueMission extends Mission {
    private final ExplorationRover rover;
    private final String rescueLocation;

    public RescueMission(String id, String name, int duration, ExplorationRover rover) {
        super(id, name, MissionType.RESCUE, duration);

        if (rover == null) {
            throw new IllegalArgumentException("RescueMission requires a rover to rescue.");
        }

        this.rover = rover;
        this.rescueLocation = (rover.getLocation() != null && !rover.getLocation().trim().isEmpty())
                ? rover.getLocation() : "Martian Surface - Sector Unknown";

        addRequiredCapability(Capability.COMMUNICATION);
        addRequiredResource(ResourceType.FUEL, 10);
        addRequiredResource(ResourceType.BATTERY_POWER, 5);
        addRequiredResource(ResourceType.SPARE_PARTS, 3);

        setOperationType(OperationType.COMMUNICATION_DEPENDENT);
    }

    public ExplorationRover getRover() {
        return rover;
    }

    public String getRescueLocation() {
        return rescueLocation;
    }

    public void complete() {
        super.complete();           // sets COMPLETED and releases the rescue agents
        rover.repair();             // condition -> OPERATIONAL
        rover.setAvailable(true);   // the rover can be assigned to missions again
        System.out.println("   [Mission " + getId() + "] Rover " + rover.getName()
                + " repaired and restored to OPERATIONAL.");
    }

    public boolean canBePerformedBy(Agent agent) {
        if (agent == null || !agent.isReady() || agent == rover) { // a broken rover can't rescue itself
            return false;
        }
        return agent.hasCapability(Capability.COMMUNICATION) || agent.hasCapability(Capability.AERIAL_RECON);
    }
}