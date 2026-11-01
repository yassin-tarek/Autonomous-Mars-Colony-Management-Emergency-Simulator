package marsops.emergencies;

import marsops.Agents.Robots.ExplorationRover;
import marsops.colony.Colony;
import marsops.missions.Mission;
import marsops.missions.RescueMission;
import marsops.AppEnums.*;

public class RoverBreakdownEmergency extends Emergency {
    private ExplorationRover rover;
    private RescueMission rescueMission;

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

    public RescueMission createRescueMission(String missionId, int duration) {
        this.rescueMission = new RescueMission(missionId, "Rescue " + rover.getName(), duration, rover);
        return rescueMission;
    }

    public boolean checkResolution() {
        if (state == EmergencyState.RESOLVED) {
            return true;
        }
        if (rescueMission != null
                && rescueMission.getState() == MissionState.COMPLETED
                && rover.getCondition() == RobotCondition.OPERATIONAL) {
            state = EmergencyState.RESOLVED;
            resolutionNote = "Rover " + rover.getName() + " repaired by rescue mission "
                    + rescueMission.getId() + ".";
            System.out.println(resolutionNote);
            return true;
        }
        return false;
    }
}

