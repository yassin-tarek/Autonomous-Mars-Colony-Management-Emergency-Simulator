package marsops.missions;
import marsops.AppEnums.MissionType;
import marsops.Agents.Agent;
import marsops.AppEnums.Capability;
import marsops.AppEnums.ResourceType;
import marsops.Facilities.Facility;

/**
 * Maintenance and repair mission targeting damaged facilities or equipment.
 * Requires equipment repair capability, spare parts, and battery power.
 */
public class RepairMission extends Mission {

    public RepairMission(String id, String name, int duration, Facility targetFacility) {
        super(id, name, MissionType.REPAIR, duration);
        setTargetFacility(targetFacility);

        // Required capabilities
        addRequiredCapability(Capability.EQUIPMENT_REPAIR);

        // Required resources
        addRequiredResource(ResourceType.SPARE_PARTS, 2);
        addRequiredResource(ResourceType.BATTERY_POWER, 5);
    }

    public RepairMission(String id, String name, int duration) {
        this(id, name, duration, null);
    }

    @Override
    public boolean canBePerformedBy(Agent agent) {
        if (agent == null || !agent.isReady()) {
            return false;
        }
        return agent.hasCapability(Capability.EQUIPMENT_REPAIR);
    }
}
