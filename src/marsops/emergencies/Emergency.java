package marsops.emergencies;

import java.util.*;
import marsops.Agents.Agent;
import marsops.AppEnums.*;
import marsops.colony.ResourceManager;

/**
 * Abstract base class for all colony emergencies.
 * Emergencies are independent of active missions — they do NOT interrupt them.
 */
public abstract class Emergency {

    private final String id;
    private final String description;
    private EmergencyState state;
    private String failureReason;
    protected final Map<ResourceType, Integer> requiredResources;
    protected final List<Agent> responders;

    public Emergency(String id, String description) {
        this.id = id;
        this.description = description;
        this.state = EmergencyState.TRIGGERED;
        this.failureReason = null;
        this.requiredResources = new EnumMap<>(ResourceType.class);
        this.responders = new ArrayList<>();
    }

    // ── Capability check ────────────────────────────────────────────────────

    /** Returns true if this agent has the capabilities needed to respond. */
    public abstract boolean canBeHandledBy(Agent agent);

    // ── State transitions ────────────────────────────────────────────────────

    public void resolve() {
        this.state = EmergencyState.RESOLVED;
        releaseResponders();
    }

    public void fail(String reason) {
        this.state = EmergencyState.UNRESOLVED;
        this.failureReason = reason;
        releaseResponders();
    }

    public void releaseResponders() {
        for (Agent a : responders) {
            a.setAvailable(true);
        }
        responders.clear();
    }

    // ── Responder management ─────────────────────────────────────────────────

    public void addResponder(Agent agent) {
        if (agent != null && !responders.contains(agent)) {
            responders.add(agent);
            agent.setAvailable(false);
        }
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getId()            { return id; }
    public String getDescription()   { return description; }
    public EmergencyState getState() { return state; }
    public String getFailureReason() { return failureReason != null ? failureReason : "N/A"; }
    public Map<ResourceType, Integer> getRequiredResources() {
        return Collections.unmodifiableMap(requiredResources);
    }

    @Override
    public String toString() {
        return String.format("Emergency[%s | %s | State=%s]", id, description, state);
    }
}
