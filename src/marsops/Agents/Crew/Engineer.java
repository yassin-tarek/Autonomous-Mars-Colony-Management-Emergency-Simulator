package marsops.Agents.Crew;

class Engineer extends CrewMember {
    public Engineer(String id, String name, String location) {
        super(id, name, location);
        addCapability(Capability.EQUIPMENT_REPAIR);
        addCapability(Capability.FIRE_RESPONSE);
    }
}
