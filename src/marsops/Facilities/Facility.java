package marsops.Facilities;
import java.util.*;
import marsops.AppEnums.ResourceType;

public abstract class Facility {
  protected String name;
    protected String location;
    protected int capacity;
    protected boolean operational;
    protected Map<ResourceType, Integer> resources;// map to have the resources and quantity for it like oxygen 200 
    protected List<Equipment> equipmentList; // Aggregation it creates empty list to add equipment in faciclity

    public Facility(String name, String location, int capacity) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.operational = true;
        this.resources = new EnumMap<>(ResourceType.class);// ENUM because its enum datattype 
        this.equipmentList = new ArrayList<>();
    }

    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public boolean isOperational() { return operational; }
    public void setOperational(boolean operational) { this.operational = operational; }
    public List<Equipment> getEquipmentList() { return equipmentList; }

    public void addEquipment(Equipment eq) {//to add el equipnments we need 
        equipmentList.add(eq);
    }

    public void addResource(ResourceType type, int amount) {
        if (amount < 0) return;
        
        resources.put(type, getResourceQuantity(type) + amount);// map 3lshan ana 3ayez value and name of it
    }

    public int getResourceQuantity(ResourceType type) {
        return resources.getOrDefault(type, 0);
    }

    // Deducts up to available amount SO FOOKING IMPORTNAT FOR MISSIONS Ai generated elsra7a but i catch the idea of the method
    public int deductResourceUpTo(ResourceType type, int amount) {
        int current = getResourceQuantity(type);
        int deducted = Math.min(current, Math.max(0, amount));
        resources.put(type, current - deducted);
        return deducted;
    }

    public Map<ResourceType, Integer> getResources() {//getter 3ady 
        return Collections.unmodifiableMap(resources);
    }

    @Override
    public String toString() {
        return String.format("%s (%s) | Operational: %s | Resources: %s | Equipment: %s",
                name, getClass().getSimpleName(), operational, resources, equipmentList);
    }

  
}
