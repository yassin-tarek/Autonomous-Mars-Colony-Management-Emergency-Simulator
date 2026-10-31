package marsops.emergencies;

import java.util.*;
import marsops.AppEnums.*;
import marsops.colony.Colony;

public abstract class Emergency {
    protected String id;
    protected String location;
    protected EmergencyState state;
    protected String resolutionNote;
    protected Set<Capability> requiredCapabilities;
    protected Map<ResourceType, Integer> requiredResources;

    public Emergency(String id, String location) {
        this.id = id;
        this.location = location;
        this.state = EmergencyState.TRIGGERED;
        this.resolutionNote = "Pending";
        this.requiredCapabilities = EnumSet.noneOf(Capability.class);
        this.requiredResources = new EnumMap<>(ResourceType.class);
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public EmergencyState getState() {
        return state;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public Set<Capability> getRequiredCapabilities() {
        return Collections.unmodifiableSet(requiredCapabilities);
    }

    public Map<ResourceType, Integer> getRequiredResources() {
        return Collections.unmodifiableMap(requiredResources);
    }

    public abstract void handle(Colony colony);

    @Override
    public String toString() {
        return String.format(
            "Emergency [%s at %s] | State: %s | Note: %s",
            getClass().getSimpleName(), location, state, resolutionNote
        );
    }
}
