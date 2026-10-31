package marsops.missions;
import marsops.AppEnums.MissionType;
import java.util.*;
import marsops.Agents.Agent;
import marsops.Agents.Crew.CrewMember;
import marsops.Agents.Robots.Robot;
import marsops.AppEnums.Capability;
import marsops.AppEnums.MissionState;
import marsops.AppEnums.OperationType;
import marsops.Facilities.Facility;
import marsops.colony.Colony;
import marsops.colony.MarsEnvironment;
import marsops.colony.ResourceManager;

/**
 * Member 3: Simulation Manager & Mission Lifecycle Coordinator.
 * Manages simulation cycles, enforces the one-active-mission rule,
 * validates polymorphic agent capabilities and multi-source resources,
 * and updates system states over simulated time.
 */
public class SimulationManager {
    private final Colony colony;
    private final MarsEnvironment environment;
    private final ResourceManager resourceManager;
    private Mission activeMission;
    private int currentCycle;

    public SimulationManager(Colony colony, MarsEnvironment environment, ResourceManager resourceManager) {
        this.colony = colony;
        this.environment = (environment != null) ? environment : (colony != null ? colony.getEnvironment() : new MarsEnvironment());
        this.resourceManager = (resourceManager != null) ? resourceManager : (colony != null ? colony.getResourceManager() : new ResourceManager(new ArrayList<>()));
        this.activeMission = null;
        this.currentCycle = 0;
    }

    public SimulationManager(Colony colony) {
        this(colony, colony != null ? colony.getEnvironment() : null, colony != null ? colony.getResourceManager() : null);
    }

    public SimulationManager(MarsEnvironment environment, ResourceManager resourceManager) {
        this(null, environment, resourceManager);
    }

    /**
     * Submits a mission following all mandatory rules:
     * 1. One active mission at a time.
     * 2. Environmental condition checks (Dust Storm, Communication Disruption).
     * 3. Candidate agent availability & polymorphic capability check (agent.canPerform(mission)).
     * 4. Resource availability check across operational facilities (All-or-Nothing).
     * 5. If accepted: marks ASSIGNED, consumes resources, locks agents.
     */
    public boolean submitMission(Mission mission, List<Agent> candidateAgents) {
        if (mission == null) {
            throw new IllegalArgumentException("Cannot submit a null mission.");
        }

        System.out.println("\n------------------------------------------------------------");
        System.out.println("  Processing Mission Submission: " + mission.getName() + " [" + mission.getId() + "]");
        System.out.println("------------------------------------------------------------");

        // Rule 1: One active mission at a time
        if (activeMission != null &&
           (activeMission.getState() == MissionState.ASSIGNED || activeMission.getState() == MissionState.IN_PROGRESS)) {
            mission.fail("Colony policy violation: Another mission (" + activeMission.getId() + ") is currently active.");
            System.out.println("REJECTED: " + mission.getFailureReason());
            return false;
        }

        // Rule 2: Mars Environment restrictions (e.g. Dust storm disallows surface exploration)
        if (mission.getOperationType() != null) {
            Optional<String> restriction = environment.checkRestriction(mission.getOperationType());
            if (restriction.isPresent()) {
                mission.fail(restriction.get());
                System.out.println("REJECTED: " + mission.getFailureReason());
                return false;
            }
        }

        // Rule 3: Candidate agents provided
        if (candidateAgents == null || candidateAgents.isEmpty()) {
            mission.fail("No agents assigned to the mission.");
            System.out.println("REJECTED: " + mission.getFailureReason());
            return false;
        }

        // Rule 4: Agent availability & polymorphic capability check
        for (Agent agent : candidateAgents) {
            if (agent == null) {
                mission.fail("Null agent reference provided.");
                System.out.println("REJECTED: " + mission.getFailureReason());
                return false;
            }

            if (!agent.isAvailable()) {
                mission.fail("Agent " + agent.getName() + " is currently unavailable.");
                System.out.println("REJECTED: " + mission.getFailureReason());
                return false;
            }

            if (!agent.isUsable()) {
                mission.fail("Agent " + agent.getName() + " is not operationally ready (damaged/fatigued).");
                System.out.println("REJECTED: " + mission.getFailureReason());
                return false;
            }

            // Polymorphic check (Ask the agent if it can perform the mission without instanceof)
            if (!agent.canPerform(mission)) {
                mission.fail("Agent does not possess the required capability.");
                System.out.println("REJECTED: " + mission.getFailureReason());
                return false;
            }
        }

        // Rule 5: Team capability completeness for Exploration (requires surface travel + scientific skill)
        if (mission.getType() == MissionType.EXPLORATION) {
            boolean hasSurfaceTravel = false;
            boolean hasResearch = false;
            for (Agent a : candidateAgents) {
                if (a.hasCapability(Capability.SURFACE_EXPLORATION) || a.getOperationTypes().contains(OperationType.SURFACE_OPERATION)) {
                    hasSurfaceTravel = true;
                }
                if (a.hasCapability(Capability.SCIENTIFIC_RESEARCH)) {
                    hasResearch = true;
                }
            }
            if (!hasSurfaceTravel || !hasResearch) {
                mission.fail("Agent team lacks complete capability profile (requires vehicle and scientific capability).");
                System.out.println("REJECTED: " + mission.getFailureReason());
                return false;
            }
        }

        // Rule 6: Resource availability check across operational facilities (All-or-Nothing)
        Optional<String> shortage = resourceManager.checkSupply(mission.getRequiredResources());
        if (shortage.isPresent()) {
            mission.fail("Colony has insufficient resources to support mission requirements: " + shortage.get());
            System.out.println("REJECTED: " + mission.getFailureReason());
            return false;
        }

        // ── All Checks Passed: Accept Mission ────────────────────────────────────
        // All-or-nothing consumption
        resourceManager.consume(mission.getRequiredResources());

        // Lock assigned agents
        for (Agent agent : candidateAgents) {
            agent.setAvailable(false);
            mission.assignAgent(agent);
        }

        mission.setState(MissionState.ASSIGNED);
        this.activeMission = mission;
        if (colony != null) {
            colony.setActiveMission(mission);
        }

        System.out.println("ACCEPTED: Mission " + mission.getId() + " is now ASSIGNED and active.");
        return true;
    }

    /**
     * Advances simulation time by the specified number of time units.
     */
    public void advanceTime(int units) {
        if (units <= 0) {
            throw new IllegalArgumentException("Time advancement units must be positive.");
        }

        for (int i = 0; i < units; i++) {
            currentCycle++;
            System.out.println("\n>>> SIMULATION CYCLE " + currentCycle + " <<<");

            if (activeMission != null) {
                // Transition ASSIGNED -> IN_PROGRESS on first step
                if (activeMission.getState() == MissionState.ASSIGNED) {
                    activeMission.setState(MissionState.IN_PROGRESS);
                    System.out.println("Mission " + activeMission.getId() + " transitioned to IN_PROGRESS.");
                }

                // Check ongoing environmental hazards
                if (activeMission.getOperationType() != null) {
                    Optional<String> hazard = environment.checkRestriction(activeMission.getOperationType());
                    if (hazard.isPresent()) {
                        System.out.println("HAZARD: Active mission aborted due to environmental hazard: " + hazard.get());
                        activeMission.fail("Active mission failed due to environment: " + hazard.get());
                        this.activeMission = null;
                        if (colony != null) colony.setActiveMission(null);
                        continue;
                    }
                }

                // Advance mission progress
                activeMission.advanceTime(1);

                // Simulation aging effects: drain robot battery and increase crew fatigue
                for (Agent agent : activeMission.getAssignedAgents()) {
                    if (agent instanceof Robot) {
                        ((Robot) agent).drainBattery(5);
                    } else if (agent instanceof CrewMember) {
                        ((CrewMember) agent).onTimeStep(true);
                    }
                }

                // Post-mission completion handling
                if (activeMission.getState() == MissionState.COMPLETED) {
                    if (activeMission.getType() == MissionType.REPAIR && activeMission.getTargetFacility() != null) {
                        activeMission.getTargetFacility().setOperational(true);
                        System.out.println("Target facility " + activeMission.getTargetFacility().getName() + " is now OPERATIONAL.");
                    }
                    this.activeMission = null;
                    if (colony != null) colony.setActiveMission(null);
                } else if (activeMission.getState() == MissionState.FAILED) {
                    this.activeMission = null;
                    if (colony != null) colony.setActiveMission(null);
                }
            } else {
                System.out.println("No active mission in progress. Colony in nominal idle state.");
            }
        }
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public Colony getColony() {
        return colony;
    }

    public MarsEnvironment getEnvironment() {
        return environment;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public Mission getActiveMission() {
        return activeMission;
    }

    public int getCurrentCycle() {
        return currentCycle;
    }
}
