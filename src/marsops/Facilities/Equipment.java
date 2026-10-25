package marsops.Facilities;
import marsops.AppEnums.Capability;

public class Equipment {
  private String name;
    private Capability providedCapability;
    private boolean damaged;

    public Equipment(String name, Capability providedCapability) {
        this.name = name;
        this.providedCapability = providedCapability;
        this.damaged = false;
    }

    public String getName() { return name; }
    public Capability getProvidedCapability() { return providedCapability; }
    public boolean isDamaged() { return damaged; }
    public void setDamaged(boolean damaged) { this.damaged = damaged; }

    @Override
    public String toString() {
        return name + (damaged ? " [DAMAGED]" : " [OK]");
    }

}
