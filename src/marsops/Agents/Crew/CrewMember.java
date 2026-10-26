package marsops.Agents;

import java.util.*;
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
        return health > 25 && fatigue < 90;
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
            "%s [%s - %s] | Loc: %s | Health: %d | Fatigue: %d | Avail: %s | Caps: %s",
            getName(), getId(), getClass().getSimpleName(),
            getLocation(), health, fatigue, isAvailable(), capabilities
        );
    }
}

class Scientist extends CrewMember {
    public Scientist(String id, String name, String location) {
        super(id, name, location);
        addCapability(Capability.SCIENTIFIC_RESEARCH);
    }
}

class Engineer extends CrewMember {
    public Engineer(String id, String name, String location) {
        super(id, name, location);
        addCapability(Capability.EQUIPMENT_REPAIR);
        addCapability(Capability.FIRE_RESPONSE);
    }
}

class MedicalOfficer extends CrewMember {
    public MedicalOfficer(String id, String name, String location) {
        super(id, name, location);
        addCapability(Capability.MEDICAL_CARE);
        addCapability(Capability.FIRE_RESPONSE);
    }
}

