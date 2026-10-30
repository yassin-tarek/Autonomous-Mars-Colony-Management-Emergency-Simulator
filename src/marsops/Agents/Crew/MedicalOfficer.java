package marsops.Agents.Crew;
import marsops.AppEnums.*;

class MedicalOfficer extends CrewMember {
    public MedicalOfficer(String id, String name, String location) {
        super(id, name, location);
        addCapability(Capability.MEDICAL_CARE);
        addCapability(Capability.FIRE_RESPONSE); // FIRE AND MEDICAL CAPABILITIES
    }
}
