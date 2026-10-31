package marsops.colony;

import java.util.*;
import marsops.Agents.Agent;
import marsops.Facilities.Facility;
import marsops.missions.Mission;

/**
 * Central coordinator aggregate root for the Mars colony.
 * Groups personnel, robotic units, facilities, environmental controller, and resource manager.
 */
public class Colony {
    private final String name;
    private final List<Agent> agents;
    private final List<Facility> facilities;
    private final MarsEnvironment environment;
    private final ResourceManager resourceManager;
    private Mission activeMission;

    public Colony(String name, MarsEnvironment environment) {
        this.name = (name != null && !name.trim().isEmpty()) ? name : "Ares Colony Alpha";
        this.agents = new ArrayList<>();
        this.facilities = new ArrayList<>();
        this.environment = (environment != null) ? environment : new MarsEnvironment();
        this.resourceManager = new ResourceManager(this.facilities);
        this.activeMission = null;
    }

    public Colony(String name) {
        this(name, new MarsEnvironment());
    }

    public Colony() {
        this("Ares Colony Alpha");
    }

    public void addAgent(Agent agent) {
        if (agent != null && !agents.contains(agent)) {
            agents.add(agent);
        }
    }

    public void addFacility(Facility facility) {
        if (facility != null && !facilities.contains(facility)) {
            facilities.add(facility);
        }
    }

    public String getName() {
        return name;
    }

    public List<Agent> getAgents() {
        return Collections.unmodifiableList(agents);
    }

    public List<Facility> getFacilities() {
        return facilities;
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

    public void setActiveMission(Mission activeMission) {
        this.activeMission = activeMission;
    }

    @Override
    public String toString() {
        return String.format("Colony: %s | Facilities: %d | Agents: %d | Active Mission: %s",
                name, facilities.size(), agents.size(),
                (activeMission != null ? activeMission.getName() : "None"));
    }
}
