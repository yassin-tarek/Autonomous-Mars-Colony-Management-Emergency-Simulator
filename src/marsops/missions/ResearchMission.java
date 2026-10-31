package marsops.missions;
import marsops.AppEnums.MissionType;
import marsops.Agents.Agent;
import marsops.AppEnums.Capability;
import marsops.AppEnums.ResourceType;
import marsops.Facilities.Facility;

/**
 * Scientific research and laboratory experiments mission.
 * Requires scientific research capabilities and battery power.
 */
public class ResearchMission extends Mission {
    private final String researchTopic;

    public ResearchMission(String id, String name, int duration, String researchTopic, Facility laboratory) {
        super(id, name, MissionType.RESEARCH, duration);
        this.researchTopic = (researchTopic != null && !researchTopic.trim().isEmpty())
                ? researchTopic : "Atmospheric Composition Analysis";
        setTargetFacility(laboratory);

        // Required capabilities
        addRequiredCapability(Capability.SCIENTIFIC_RESEARCH);

        // Required resources
        addRequiredResource(ResourceType.BATTERY_POWER, 5);
    }

    public ResearchMission(String id, String name, int duration, String researchTopic) {
        this(id, name, duration, researchTopic, null);
    }

    public ResearchMission(String id, String name, int duration) {
        this(id, name, duration, "Martian Soil Analysis", null);
    }

    public String getResearchTopic() {
        return researchTopic;
    }

    @Override
    public boolean canBePerformedBy(Agent agent) {
        if (agent == null || !agent.isReady()) {
            return false;
        }
        return agent.hasCapability(Capability.SCIENTIFIC_RESEARCH);
    }
}
