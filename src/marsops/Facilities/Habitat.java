package marsops.Facilities;

public class Habitat extends Facility {
    //COMPOSITION HAS A STRONG
    private final LifeSupportSystem lifeSupport;

    public Habitat(String name, String location, int capacity) {
        super(name, location, capacity);
        this.lifeSupport = new LifeSupportSystem();
    }

    public LifeSupportSystem getLifeSupport() { return lifeSupport; }
}
