package marsops.Agents.Crew;
import java.util.*;
import marsops.Agents.Agent;
import marsops.AppEnums.*;

public abstract class CrewMember extends Agent {
    protected int fatigue;
    private int health;
    private final Set<Capability> capabilities = new HashSet<>();

    public CrewMember(String id, String name, String location) {
        super(id, name, location);
        this.fatigue = 0;
        this.health = 100;
    }

    public int getFatigue() {
        return fatigue;
    }

    public void setFatigue(int fatigue) {
        this.fatigue = Math.max(0, Math.min(100, fatigue));
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(100, health));
    }

    @Override
    public boolean isUsable() {
        return health > 30 && fatigue < 90; //to be useable crew member these conditions should be availabe
    }

    @Override
    public Set<Capability> getCapabilities() {
        return Collections.unmodifiableSet(capabilities);
    }

    protected void addCapability(Capability capability) {
        capabilities.add(capability);
    }

    public void onTimeStep(boolean onMission) {
        if (onMission) {
            setFatigue(fatigue + 10);
        } else {
            setFatigue(fatigue - 5);
        }
    }

    @Override
    public String toString() {
        return String.format(
            "%s [%s - %s] | Loc: %s | Health: %d | Fatigue: %d | Avail: %s | Caps: %s",    // to be easier in print i will overide teh tostring
            getName(), getId(), getClass().getSimpleName(),
            getLocation(), health, fatigue, isAvailable(), capabilities
        );
    }
}


