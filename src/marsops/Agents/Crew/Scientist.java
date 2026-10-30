package marsops.Agents.Crew;
import marsops.AppEnums.*;

public class Scientist extends CrewMember {
    public Scientist(String id, String name, String location) {
        super(id, name, location);
        addCapability(Capability.SCIENTIFIC_RESEARCH);// HE ONLY GOT THE SCIENTIFIC RESEARCH 
    }
}
