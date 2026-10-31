package marsops.emergencies;

import marsops.Agents.Agent;
import marsops.Agents.Crew.CrewMember;
import marsops.Agents.Robots.Robot;
import marsops.Facilities.Habitat;
import marsops.AppEnums.*;
import marsops.colony.Colony;

public class OxygenLeakEmergency extends Emergency {
    private Habitat affectedHabitat;
    private int leakOxygenAmount;

    public OxygenLeakEmergency(String id, Habitat affectedHabitat, int leakOxygenAmount) {
        super(id, affectedHabitat.getName());
        
        this.affectedHabitat = affectedHabitat;
        this.leakOxygenAmount = leakOxygenAmount;

        requiredCapabilities.add(Capability.EQUIPMENT_REPAIR);
        requiredResources.put(ResourceType.SPARE_PARTS, 2);
        requiredResources.put(ResourceType.BATTERY_POWER, 5);
    }

    @Override
    public void handle(Colony colony) {
        System.out.println("Oxygen leak in " + affectedHabitat.getName());

        affectedHabitat.getLifeSupport().setFunctional(false);

    affectedHabitat.deductResourceUpTo(ResourceType.OXYGEN, leakOxygenAmount);

        Agent human = null;
        Agent robot = null;

        for (Agent agent : colony.getAgents()) {
            if (!agent.isReady()) {
                continue;
            }

            if (agent instanceof CrewMember &&
                agent.hasCapability(Capability.EQUIPMENT_REPAIR)) {
                human = agent;
            }

            if (agent instanceof Robot &&
                agent.hasCapability(Capability.EQUIPMENT_REPAIR)) {
                robot = agent;
            }
        }

        if (human == null || robot == null) {
            failLeakResponse("Required human and repair robot are unavailable.");
            return;
        }

        if (colony.getResourceManager().checkSupply(requiredResources).isPresent()) {
            failLeakResponse("Insufficient resources to repair the leak.");
            return;
        }

        human.setAvailable(false);
        robot.setAvailable(false);

        human.moveTo(affectedHabitat.getName());
        robot.moveTo(affectedHabitat.getName());

        colony.getResourceManager().consume(requiredResources);

        affectedHabitat.getLifeSupport().setFunctional(true);
        affectedHabitat.setOperational(true);

        human.setAvailable(true);
        robot.setAvailable(true);

        state = EmergencyState.RESOLVED;
        resolutionNote = "Leak repaired by " + human.getName()
            + " and " + robot.getName() + ".";

        System.out.println(resolutionNote);
    }

    private void failLeakResponse(String reason) {
        int additionalLoss = affectedHabitat.deductResourceUpTo(
            ResourceType.OXYGEN, leakOxygenAmount
        );

        state = EmergencyState.UNRESOLVED;
        resolutionNote = reason + " Additional oxygen lost: " + additionalLoss;
    }
}
