package marsops.emergencies;

import java.util.*;
import marsops.Agents.Agent;
import marsops.AppEnums.*;
import marsops.colony.ResourceManager;

/**
 * Dispatches emergency-response teams to active emergencies.
 * Emergency handling is independent of colony missions.
 */
public class EmergencyDispatcher {

    /**
     * Attempts to handle an OxygenLeakEmergency.
     *
     * <p>Protocol:
     * <ol>
     *   <li>Apply the initial oxygen leak to the habitat.</li>
     *   <li>Find at least one available agent capable of EQUIPMENT_REPAIR.</li>
     *   <li>Check colony resources cover SPARE_PARTS and BATTERY_POWER.</li>
     *   <li>If ok → assign responders, consume resources, resolve emergency.</li>
     *   <li>If not → apply unresolved penalty (extra oxygen loss) and fail.</li>
     * </ol>
     *
     * @param emergency       the triggered oxygen-leak emergency
     * @param availableAgents all colony agents to search through
     * @param resourceManager the colony resource manager
     * @return true if resolved, false if unresolved
     */
    public boolean handleOxygenLeak(OxygenLeakEmergency emergency,
                                     List<Agent> availableAgents,
                                     ResourceManager resourceManager) {

        System.out.println("[Dispatcher] Responding to: " + emergency.getDescription());

        // Step 1 – apply the initial damage
        emergency.applyInitialLeak();
        System.out.println("[Dispatcher] Initial leak applied.");

        // Step 2 – find capable, ready responders
        List<Agent> responders = new ArrayList<>();
        for (Agent agent : availableAgents) {
            if (agent.isReady() && emergency.canBeHandledBy(agent)) {
                responders.add(agent);
            }
        }

        if (responders.isEmpty()) {
            System.out.println("[Dispatcher] No capable responders available. Applying unresolved penalty.");
            emergency.applyUnresolvedPenalty();
            emergency.fail("No available agent with EQUIPMENT_REPAIR capability");
            return false;
        }

        // Step 3 – verify resources
        Optional<String> resourceCheck = resourceManager.checkSupply(emergency.getRequiredResources());
        if (resourceCheck.isPresent()) {
            System.out.println("[Dispatcher] Insufficient resources: " + resourceCheck.get()
                               + ". Applying unresolved penalty.");
            emergency.applyUnresolvedPenalty();
            emergency.fail("Resource shortage: " + resourceCheck.get());
            return false;
        }

        // Step 4 – dispatch: assign responders, consume resources, resolve
        for (Agent responder : responders) {
            emergency.addResponder(responder);
            System.out.println("[Dispatcher] Assigned responder: " + responder.getName());
        }

        resourceManager.consume(emergency.getRequiredResources());
        System.out.println("[Dispatcher] Resources consumed: " + emergency.getRequiredResources());

        emergency.resolve();   // also releases responders (sets available=true)
        System.out.println("[Dispatcher] Emergency resolved successfully.");
        return true;
    }
}
