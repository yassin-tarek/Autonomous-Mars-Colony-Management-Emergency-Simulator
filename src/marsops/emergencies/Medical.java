package marsops.emergencies;

import marsops.Agents.Agent;
import marsops.Agents.Crew.CrewMember;
import marsops.Facilities.Facility;
import marsops.colony.Colony;
import marsops.AppEnums.*;

public class MedicalEmergency extends Emergency {
    private CrewMember patient;
    private Facility medicalFacility;

    public MedicalEmergency(String id, CrewMember patient, Facility medicalFacility) {
        super(id, medicalFacility.getName());

        this.patient = patient;
        this.medicalFacility = medicalFacility;

        requiredCapabilities.add(Capability.MEDICAL_CARE);
        requiredResources.put(ResourceType.MEDICAL_SUPPLIES, 5);
        requiredResources.put(ResourceType.OXYGEN, 5);
    }

    @Override
    public void handle(Colony colony) {
        System.out.println("Medical Emergency for " + patient.getName());

        if (!medicalFacility.isOperational()) {
            state = EmergencyState.UNRESOLVED;
            resolutionNote = "Medical facility (" + medicalFacility.getName()
                    + ") is not operational.";
            System.out.println(resolutionNote);
            return;
        }

        Agent medic = null;

        for (Agent agent : colony.getAgents()) {
            if (agent != patient &&
                agent.isReady() &&
                agent.hasCapability(Capability.MEDICAL_CARE)) {

                medic = agent;
                break;
            }
        }

        if (medic == null) {
            state = EmergencyState.UNRESOLVED;
            resolutionNote = "No available Medical Officer.";
            System.out.println(resolutionNote);
            return;
        }

        if (colony.getResourceManager().checkSupply(requiredResources).isPresent()) {
            state = EmergencyState.UNRESOLVED;
            resolutionNote = "Insufficient medical supplies or oxygen.";
            System.out.println(resolutionNote);
            return;
        }

        medic.setAvailable(false);
        medic.moveTo(medicalFacility.getName());

        patient.moveTo(medicalFacility.getName());

        if (colony.getResourceManager().consume(requiredResources).isPresent()) {
            medic.setAvailable(true);

            state = EmergencyState.UNRESOLVED;
            resolutionNote = "Failed to consume medical resources.";
            System.out.println(resolutionNote);
            return;
        }

        patient.setHealth(patient.getHealth() + 40);

        medic.setAvailable(true);

        state = EmergencyState.RESOLVED;

        resolutionNote = "Patient " + patient.getName()
                + " treated by " + medic.getName()
                + ". Health restored to " + patient.getHealth() + ".";

        System.out.println(resolutionNote);
    }
}
