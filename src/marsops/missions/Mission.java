package marsops.missions;

import java.util.*;
import marsops.Agents.Agent;
import marsops.AppEnums.*;
import marsops.Facilities.Facility;

/**
 * Abstract base class for all colony missions in MARS-OPS.
 * Manages required resources, required capabilities, assigned agents,
 * operational environment types, and lifecycle states (ASSIGNED -> IN_PROGRESS -> COMPLETED/FAILED).
 */
public abstract class Mission {
    private final String id;
    private final String name;
    private final MissionType type;
    private final int duration;
    private int elapsedTime;
    private MissionState state;
    private String failureReason;

    private final Map<ResourceType, Integer> requiredResources;
    private final Set<Capability> requiredCapabilities;
    private final List<Agent> assignedAgents;
    private Facility targetFacility;
    private OperationType operationType;

    public Mission(String id, String name, MissionType type, int duration) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Mission must have an ID.");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Mission duration must be positive: " + duration);
        }
        this.id = id;
        this.name = (name == null || name.trim().isEmpty()) ? id : name;
        this.type = type;
        this.duration = duration;
        this.elapsedTime = 0;
        this.state = MissionState.ASSIGNED;
        this.failureReason = "None";
        this.requiredResources = new EnumMap<>(ResourceType.class);
        this.requiredCapabilities = new HashSet<>();
        this.assignedAgents = new ArrayList<>();
        this.operationType = null;
    }

    public void addRequiredResource(ResourceType type, int amount) {
        if (type != null && amount > 0) {
            this.requiredResources.put(type, this.requiredResources.getOrDefault(type, 0) + amount);
        }
    }

    public void addRequiredCapability(Capability capability) {
        if (capability != null) {
            this.requiredCapabilities.add(capability);
        }
    }

    public void assignAgent(Agent agent) {
        if (agent != null && !this.assignedAgents.contains(agent)) {
            this.assignedAgents.add(agent);
        }
    }

    public void setTargetFacility(Facility facility) {
        this.targetFacility = facility;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    /**
     * Marks the mission as FAILED, records the reason, and releases all agents.
     * Rule: A rejected/failed mission must have a clear reason and never remain active.
     */
    public void fail(String reason) {
        this.state = MissionState.FAILED;
        this.failureReason = (reason == null || reason.trim().isEmpty()) ? "Unknown reason" : reason;
        releaseAgents();
    }

    /**
     * Marks the mission as COMPLETED and releases assigned agents.
     */
    public void complete() {
        this.state = MissionState.COMPLETED;
        releaseAgents();
    }

    /**
     * Releases all assigned agents back to available status.
     */
    public void releaseAgents() {
        for (Agent agent : assignedAgents) {
            if (agent != null) {
                agent.setAvailable(true);
            }
        }
    }

    /**
     * Advances mission duration by the specified time units.
     */
    public void advanceTime(int timeUnits) {
        if (this.state != MissionState.IN_PROGRESS) {
            return;
        }
        this.elapsedTime += timeUnits;
        System.out.println("   [Mission " + id + "] Progress: " + elapsedTime + "/" + duration + " time units.");

        if (this.elapsedTime >= this.duration) {
            complete();
            System.out.println("   [Mission " + id + "] Successfully COMPLETED.");
        }
    }

    /**
     * Checks polymorphically whether an agent is ready and possesses any of the required capabilities.
     */
    public boolean canBePerformedBy(Agent agent) {
        if (agent == null || !agent.isReady()) {
            return false;
        }
        if (requiredCapabilities.isEmpty()) {
            return true;
        }
        for (Capability cap : requiredCapabilities) {
            if (agent.hasCapability(cap)) {
                return true;
            }
        }
        return false;
    }

    // ── Getters and Setters ───────────────────────────────────────────────────
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MissionType getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public MissionState getState() {
        return state;
    }

    public void setState(MissionState state) {
        this.state = state;
    }

  public MissionState getStatus() {
    return this.state;
}

    public String getFailureReason() {
        return failureReason;
    }

    public Map<ResourceType, Integer> getRequiredResources() {
        return Collections.unmodifiableMap(requiredResources);
    }

    public Set<Capability> getRequiredCapabilities() {
        return Collections.unmodifiableSet(requiredCapabilities);
    }

    public List<Agent> getAssignedAgents() {
        return Collections.unmodifiableList(assignedAgents);
    }

    public Facility getTargetFacility() {
        return targetFacility;
    }

    @Override
    public String toString() {
        return String.format("%s [%s - %s] | State: %s | Elapsed: %d/%d | Agents: %d",
                name, id, type, state, elapsedTime, duration, assignedAgents.size());
    }
}
