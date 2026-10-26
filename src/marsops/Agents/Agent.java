package marsops.Agents;
import java.util.*;
import marsops.AppEnums.*;

public abstract class Agent {
public static final String SURFACE = "Martian Surface";

    private final String id;
private final String name;
private String location;
private boolean isAvailable;

protected Agent(String id, String name, String location){
    this.id = id;
    this.name = name;
    this.location = location;
    this.isAvailable = true;
}

public String getId() { return id; }
public String getName() { return name; }
public String getLocation() { return location; }
public boolean isAvailable() { return isAvailable; }

public void setAvailable(boolean available) { isAvailable = available; }
public void moveTo(String location) { this.location = location; }

public abstract boolean isUsable(); //for robot check not damaged and battery>0, for crewmember check health and fatigue
    public abstract Set<Capability> getCapabilities();  // will be overridden in subclasses to return the specific capabilities of the agent
 
    public Set<OperationType> getOperationTypes() {
        return Set.of();    // will be overridden to return set of Environment condition dependencies (EnvironmentCondition enum in AppEnums.java) 
    }                       // default is empty set for crew and repair bots.

    public boolean hasCapability(Capability capability) {
        return getCapabilities().contains(capability);
    }
 
    public boolean isReady() {
        return isAvailable && isUsable();
    }

    // Add mission checks here.
}
