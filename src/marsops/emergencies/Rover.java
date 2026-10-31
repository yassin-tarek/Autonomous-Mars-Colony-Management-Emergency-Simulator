package marsops.emergencies;

import marsops.Agents.Robots.ExplorationRover;
import marsops.colony.Colony;
import marsops.missions.Mission;
import marsops.AppEnums.*;

public class RoverBreakdownEmergency extends Emergency {
    private ExplorationRover rover;

    public RoverBreakdownEmergency(String id, ExplorationRover rover) {
        super(id, rover.getLocation());

        this.rover = rover;

        requiredCapabilities.add(Capability.EQUIPMENT_REPAIR);
        requiredResources.put(ResourceType.SPARE_PARTS, 3);
    }

    @Override
    public void handle(Colony colony) {
        String breakdownLocation = rover.getLocation();

        System.out.println("Rover Breakdown: " + rover.getName()
                + " at " + breakdownLocation);

        rover.damage();

        Mission active = colony.getActiveMission();

        if (active != null &&
            active.getAssignedAgents().contains(rover)) {

            active.fail("Rover " + rover.getName()
                    + " broke down on the Martian surface.");

            colony.setActiveMission(null);
        }

        state = EmergencyState.UNRESOLVED;

        resolutionNote = "Rover immobilized at " + breakdownLocation
                + ". Requires Repair/Rescue operation (3 SPARE_PARTS).";

        System.out.println(resolutionNote);
    }
}
