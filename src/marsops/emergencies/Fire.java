package marsops.emergencies;

import marsops.Agents.Agent;
import marsops.Agents.Crew.CrewMember;
import marsops.Agents.Robots.Robot;
import marsops.Facilities.Laboratory;
import marsops.Facilities.Equipment;
import marsops.AppEnums.*;
import marsops.colony.Colony;

public class FireEmergency extends Emergency {
    private Laboratory affectedLab;
    private int oxygenBurned;

    public FireEmergency(String id, Laboratory affectedLab, int oxygenBurned) {
        super(id, affectedLab.getName());

        this.affectedLab = affectedLab;
        this.oxygenBurned = oxygenBurned;

        requiredCapabilities.add(Capability.FIRE_RESPONSE);
        requiredResources.put(ResourceType.WATER, 20);
        requiredResources.put(ResourceType.OXYGEN, 10);
    }

    @Override
    public void handle(Colony colony) {
        System.out.println("Fire in " + affectedLab.getName());

        affectedLab.setOperational(false);
        affectedLab.deductResourceUpTo(ResourceType.OXYGEN, oxygenBurned);

        for (Equipment equipment : affectedLab.getEquipmentList()) {
            equipment.setDamaged(true);
        }

        boolean personnelInside = false;

        for (Agent agent : colony.getAgents()) {
            if (agent instanceof CrewMember &&
                agent.getLocation().equals(affectedLab.getName())) {

                personnelInside = true;

                CrewMember crew = (CrewMember) agent;
                crew.setHealth(crew.getHealth() - 30);
            }
        }

        Agent engineer = null;
        Agent repairRobot = null;
        Agent medic = null;

        for (Agent agent : colony.getAgents()) {
            if (!agent.isReady()) {
                continue;
            }

            if (agent instanceof CrewMember &&
                agent.hasCapability(Capability.FIRE_RESPONSE) &&
                agent.hasCapability(Capability.EQUIPMENT_REPAIR)) {

                engineer = agent;
            }

            if (agent instanceof Robot &&
                agent.hasCapability(Capability.EQUIPMENT_REPAIR)) {

                repairRobot = agent;
            }
        }

        if (personnelInside) {
            for (Agent agent : colony.getAgents()) {
                if (agent.isReady() &&
                    agent instanceof CrewMember &&
                    agent.hasCapability(Capability.MEDICAL_CARE) &&
                    agent != engineer) {

                    medic = agent;
                    break;
                }
            }

            requiredResources.put(ResourceType.MEDICAL_SUPPLIES, 5);
        }

        if (engineer == null || repairRobot == null ||
            (personnelInside && medic == null)) {

            state = EmergencyState.UNRESOLVED;
            resolutionNote = "Required fire responders are unavailable.";
            System.out.println(resolutionNote);
            return;
        }

        if (colony.getResourceManager().checkSupply(requiredResources).isPresent()) {
            state = EmergencyState.UNRESOLVED;
            resolutionNote = "Insufficient resources for fire response.";
            System.out.println(resolutionNote);
            return;
        }

        engineer.setAvailable(false);
        repairRobot.setAvailable(false);

        engineer.moveTo(affectedLab.getName());
        repairRobot.moveTo(affectedLab.getName());

        if (personnelInside) {
            medic.setAvailable(false);
            medic.moveTo(affectedLab.getName());
        }

        colony.getResourceManager().consume(requiredResources);

        affectedLab.setOperational(true);

        engineer.setAvailable(true);
        repairRobot.setAvailable(true);

        if (personnelInside) {
            medic.setAvailable(true);
        }

        state = EmergencyState.RESOLVED;

        resolutionNote = "Fire extinguished by Engineer and Repair Robot."
            + (personnelInside ? " Medical Officer dispatched." : "")
            + " Equipment still requires repair.";

        System.out.println(resolutionNote);
    }
}
