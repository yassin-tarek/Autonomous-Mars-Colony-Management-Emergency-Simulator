package marsops.Agents.Robots;
import marsops.Agents.Agent;

import java.util.Set;

import marsops.AppEnums.*;

public abstract class Robot extends Agent {
    public static final int MAX_BATTERY = 100;
    private int batteryLevel;
     private RobotCondition condition;
     private final Set<Capability> capabilities;
    
    protected Robot(String id, String name, String location, int batteryLevel, Set<Capability> capabilities) {
        super(id, name, location);
        this.batteryLevel = Math.max(0, Math.min(MAX_BATTERY, batteryLevel));
        this.capabilities = Set.copyOf(capabilities);
    }

    public RobotCondition getCondition() {return condition;}
    public int getBatteryLevel() {return batteryLevel;}
    public void damage() { condition = RobotCondition.DAMAGED; }
    public void repair() { condition = RobotCondition.OPERATIONAL; }

    public void drainBattery(int amount) {
        batteryLevel = Math.max(0, batteryLevel - Math.max(0, amount)); // Ensure battery level doesn't go below 0
    }
    public void recharge(int amount) {
        batteryLevel = Math.min(MAX_BATTERY, batteryLevel + Math.max(0, amount)); // Ensure battery level doesn't exceed MAX_BATTERY
    }


    public boolean isUsable() {
        return condition == RobotCondition.OPERATIONAL && batteryLevel > 0;
    }

    public Set<Capability> getCapabilities() {
        return capabilities;
    }

    public String toString() {
        return String.format("Name: %s (%s) | Battery: %d%% | Status: %s | Location: %s | %s",
                getName(), getClass().getSimpleName(), batteryLevel,  // getSimpleName() returns the class name without the package
                condition == RobotCondition.DAMAGED ? "DAMAGED" : "OPERATIONAL", getLocation(),
                isAvailable() ? "Available" : "Busy");
    }
}
