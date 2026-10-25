package marsops;
import java.util.*;

public class AppEnums {

public enum ResourceType {
    OXYGEN, WATER, FOOD, FUEL, BATTERY_POWER, MEDICAL_SUPPLIES, SPARE_PARTS
}

public enum Capability {
    SCIENTIFIC_RESEARCH,
    EQUIPMENT_REPAIR,
    MEDICAL_CARE,
    SURFACE_EXPLORATION,
    AERIAL_RECON,
    COMMUNICATION,
    FIRE_RESPONSE
}

public enum MissionState {
    ASSIGNED, IN_PROGRESS, COMPLETED, FAILED
}


public enum EmergencyState {
    TRIGGERED, RESOLVED, UNRESOLVED
}


public enum OperationType {
    SURFACE_OPERATION,
    REMOTE_DRONE_CONTROL,
    COMMUNICATION_DEPENDENT
}

public enum EnvironmentCondition {
    NORMAL(Map.of()),
    DUST_STORM(Map.of(
        OperationType.SURFACE_OPERATION, "Surface operations are not allowed during a dust storm")),
    COMMUNICATION_DISRUPTION(Map.of(
        OperationType.REMOTE_DRONE_CONTROL, "Remote drones cannot be controlled during a communication disruption",
        OperationType.COMMUNICATION_DEPENDENT, "Communication-dependent operations are unavailable during a communication disruption"));

        // dict of {condition, {operation: restriction message}}

    private final Map<OperationType, String> restrictions;

    EnvironmentCondition(Map<OperationType, String> restrictions) {
        this.restrictions = restrictions;
    }
    
    public Optional<String> restrictionFor(OperationType operation) {
        return Optional.ofNullable(restrictions.get(operation)); // we use optional bec we can either have a restriction or no restriction (fixes NPE)
    }
}

}