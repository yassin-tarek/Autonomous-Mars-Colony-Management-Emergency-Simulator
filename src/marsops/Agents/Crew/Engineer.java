package marsops.Agents.Crew;
import marsops.AppEnums.*;

class Engineer extends CrewMember {
    public Engineer(String id, String name, String location) {
        super(id, name, location);
        addCapability(Capability.EQUIPMENT_REPAIR);// the engineer has the fire and equipment capabilitites 
        addCapability(Capability.FIRE_RESPONSE);// KEFY KDA 
    }
}
