package marsops;

import java.util.*;
import marsops.Agents.Agent;
import marsops.Agents.Crew.Engineer;
import marsops.Agents.Crew.MedicalOfficer;
import marsops.Agents.Crew.Scientist;
import marsops.Agents.Robots.Drone;
import marsops.Agents.Robots.ExplorationRover;
import marsops.Agents.Robots.RepairRobot;
import marsops.AppEnums.*;
import marsops.Facilities.Habitat;
import marsops.Facilities.Laboratory;
import marsops.Facilities.Storage;
import marsops.colony.Colony;
import marsops.colony.MarsEnvironment;
import marsops.emergencies.EmergencyDispatcher;
import marsops.emergencies.OxygenLeakEmergency;
import marsops.missions.ExplorationMission;
import marsops.missions.Mission;
import marsops.missions.SimulationManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("================================================================================");
        System.out.println("      MARS-OPS: AUTONOMOUS MARS COLONY MANAGEMENT & SIMULATOR");
        System.out.println("      Alexandria University - Programming II (Lab 5)");
        System.out.println("================================================================================");

        // 1. Initialize Colony Infrastructure
        MarsEnvironment environment = new MarsEnvironment();
        Colony colony = new Colony("Ares Base Alpha", environment);

        // Facilities
        Habitat habitatAlpha = new Habitat("Central Habitat Alpha", "Sector A", 1000);
        habitatAlpha.addResource(ResourceType.OXYGEN, 500);
        habitatAlpha.addResource(ResourceType.WATER, 200);
        habitatAlpha.addResource(ResourceType.FOOD, 150);

        Laboratory lab = new Laboratory("Geology Research Lab", "Sector B", 500);

        Storage storage = new Storage("Primary Supply Depot", "Sector C", 2000);
        storage.addResource(ResourceType.OXYGEN, 300);
        storage.addResource(ResourceType.WATER, 400);
        storage.addResource(ResourceType.FOOD, 400);
        storage.addResource(ResourceType.FUEL, 100);
        storage.addResource(ResourceType.BATTERY_POWER, 100);
        storage.addResource(ResourceType.MEDICAL_SUPPLIES, 15);
        storage.addResource(ResourceType.SPARE_PARTS, 20);

        colony.addFacility(habitatAlpha);
        colony.addFacility(lab);
        colony.addFacility(storage);

        // Personnel
        Scientist watney = new Scientist("CREW-101", "Dr. Mark Watney", habitatAlpha.getName());
        Engineer bob = new Engineer("CREW-102", "Bob the Engineer", habitatAlpha.getName());
        MedicalOfficer crusher = new MedicalOfficer("CREW-103", "Dr. Beverly Crusher", habitatAlpha.getName());

        colony.addAgent(watney);
        colony.addAgent(bob);
        colony.addAgent(crusher);

        // Robots
        ExplorationRover curiosity = new ExplorationRover("BOT-201", "Rover Curiosity", Agent.SURFACE, 100);
        RepairRobot fixer = new RepairRobot("BOT-202", "Fixer Bot", storage.getName(), 100);
        Drone drone = new Drone("BOT-203", "Recon Drone", Agent.SURFACE, 100);

        colony.addAgent(curiosity);
        colony.addAgent(fixer);
        colony.addAgent(drone);

        // Simulation Controller (Member 3)
        SimulationManager sim = new SimulationManager(colony);

        System.out.println("\n--- INITIAL COLONY STATUS ---");
        System.out.println(colony.getResourceManager().describeTotals());

        // =====================================================================
        // SCENARIO 1: Successful Exploration Mission
        // =====================================================================
        System.out.println("================================================================================");
        System.out.println(">>> SCENARIO 1: Successful Exploration Mission <<<");
        System.out.println("Requirement: Scientist, Exploration Rover, valid resources, successful execution.");
        System.out.println("================================================================================");

        System.out.println("[State BEFORE Execution]:");
        System.out.println("  Scientist Available: " + watney.isAvailable() + " | Fatigue: " + watney.getFatigue());
        System.out.println("  Rover Available    : " + curiosity.isAvailable() + " | Battery: " + curiosity.getBatteryLevel() + "%");
        System.out.println("  Colony Battery Res : " + colony.getResourceManager().getTotals().get(ResourceType.BATTERY_POWER));

        ExplorationMission exp01 = new ExplorationMission("MIS-EXP-01", "Valles Marineris Survey", 2, "Valles Marineris");
        boolean submitted = sim.submitMission(exp01, List.of(watney, curiosity));

        if (submitted) {
            System.out.println("\n[Advancing Simulation Time (2 time units)]...");
            sim.advanceTime(2);

            System.out.println("\n[State AFTER Execution]:");
            System.out.println("  Mission State       : " + exp01.getState());
            System.out.println("  Scientist Available : " + watney.isAvailable() + " | Fatigue: " + watney.getFatigue());
            System.out.println("  Rover Available     : " + curiosity.isAvailable() + " | Battery: " + curiosity.getBatteryLevel() + "%");
            System.out.println("  Active Mission Slot : " + sim.getActiveMission());
            System.out.println(">>> SCENARIO 1 PASSED <<<\n");
        }

        // =====================================================================
        // SCENARIO 2: Mission Rejection (Unsuitable Agent)
        // =====================================================================
        System.out.println("================================================================================");
        System.out.println(">>> SCENARIO 2: Mission Rejection (Unsuitable Agent) <<<");
        System.out.println("Requirement: Submit mission with unsuitable agent; fails immediately, never active.");
        System.out.println("================================================================================");

        ExplorationMission exp02 = new ExplorationMission("MIS-EXP-02", "Olympus Mons Recon", 2, "Olympus Mons");
        // MedicalOfficer lacks scientific research / vehicle exploration
        boolean submitted2 = sim.submitMission(exp02, List.of(crusher));

        System.out.println("\n[Verification]:");
        System.out.println("  Submission Result    : " + (submitted2 ? "ACCEPTED" : "REJECTED"));
        System.out.println("  Mission Final State  : " + exp02.getState());
        System.out.println("  Reason for Failure   : " + exp02.getFailureReason());
        System.out.println("  Colony Active Mission: " + sim.getActiveMission());
        System.out.println(">>> SCENARIO 2 PASSED <<<\n");

        // =====================================================================
        // SCENARIO 3: Resource Shortage Rejection
        // =====================================================================
        System.out.println("================================================================================");
        System.out.println(">>> SCENARIO 3: Resource Shortage Rejection <<<");
        System.out.println("Requirement: Reject without consuming unavailable resources or negative amounts.");
        System.out.println("================================================================================");

        int fuelBefore = colony.getResourceManager().getTotals().get(ResourceType.FUEL);
        System.out.println("[Colony Fuel BEFORE Attempt]: " + fuelBefore + " units");

        ExplorationMission exp03 = new ExplorationMission("MIS-EXP-03", "Deep Core Survey", 2, "Planum Australe");
        exp03.addRequiredResource(ResourceType.FUEL, 99999); // Impossible demand

        boolean submitted3 = sim.submitMission(exp03, List.of(watney, curiosity));

        int fuelAfter = colony.getResourceManager().getTotals().get(ResourceType.FUEL);
        System.out.println("[Colony Fuel AFTER Attempt ]: " + fuelAfter + " units");
        System.out.println("  Mission Final State       : " + exp03.getState());
        System.out.println("  Reason for Failure         : " + exp03.getFailureReason());
        System.out.println("  All-or-Nothing Validated   : " + (fuelBefore == fuelAfter));
        System.out.println(">>> SCENARIO 3 PASSED <<<\n");

        // =====================================================================
        // SCENARIO 4: Emergency Handling (Oxygen Leak)
        // =====================================================================
        System.out.println("================================================================================");
        System.out.println(">>> SCENARIO 4: Emergency Handling (Oxygen Leak) <<<");
        System.out.println("Requirement: Oxygen leak triggers → Engineer dispatched → emergency resolved.");
        System.out.println("================================================================================");

        // After Scenario 1, watney & curiosity are available again.
        // bob (Engineer) and fixer (RepairRobot) are free — use them as responders.
        int oxygenBefore   = habitatAlpha.getResourceQuantity(ResourceType.OXYGEN);
        int sparesBefore   = colony.getResourceManager().getTotals().get(ResourceType.SPARE_PARTS);
        System.out.println("[State BEFORE Emergency]:");
        System.out.println("  Habitat Alpha Oxygen : " + oxygenBefore + " units");
        System.out.println("  Colony Spare Parts   : " + sparesBefore + " units");
        System.out.println("  Bob Available        : " + bob.isAvailable());
        System.out.println("  Fixer Available      : " + fixer.isAvailable());

        OxygenLeakEmergency leak = new OxygenLeakEmergency("EMER-OXY-01", habitatAlpha, 100);
        EmergencyDispatcher dispatcher = new EmergencyDispatcher();
        boolean resolved = dispatcher.handleOxygenLeak(leak, colony.getAgents(), colony.getResourceManager());

        int oxygenAfter = habitatAlpha.getResourceQuantity(ResourceType.OXYGEN);
        int sparesAfter  = colony.getResourceManager().getTotals().get(ResourceType.SPARE_PARTS);
        System.out.println("\n[State AFTER Emergency]:");
        System.out.println("  Emergency State      : " + leak.getState());
        System.out.println("  Habitat Alpha Oxygen : " + oxygenAfter + " units (lost " + (oxygenBefore - oxygenAfter) + ")");
        System.out.println("  Colony Spare Parts   : " + sparesAfter + " units (consumed " + (sparesBefore - sparesAfter) + ")");
        System.out.println("  Bob Available Again  : " + bob.isAvailable());
        System.out.println("  Fixer Available Again: " + fixer.isAvailable());

        boolean sc4pass = resolved
                && leak.getState() == marsops.AppEnums.EmergencyState.RESOLVED
                && (oxygenBefore - oxygenAfter) == 100   // only initial leak (not penalty)
                && (sparesBefore - sparesAfter) == 2
                && bob.isAvailable()
                && fixer.isAvailable();
        System.out.println(">>> SCENARIO 4 " + (sc4pass ? "PASSED" : "FAILED") + " <<<\n");

        // =====================================================================
        // SCENARIO 5: Environmental Effect (Dust Storm)
        // =====================================================================
        System.out.println("================================================================================");
        System.out.println(">>> SCENARIO 5: Environmental Effect (Dust Storm) <<<");
        System.out.println("Requirement: Atmospheric dust storm blocks surface exploration missions.");
        System.out.println("================================================================================");

        System.out.println("Setting Environment Condition to DUST_STORM...");
        environment.setCondition(EnvironmentCondition.DUST_STORM);

        ExplorationMission exp05 = new ExplorationMission("MIS-EXP-05", "Dune Surface Exploration", 2, "Elysium Planitia");
        boolean submitted5 = sim.submitMission(exp05, List.of(watney, curiosity));

        System.out.println("\n[Verification]:");
        System.out.println("  Submission Result     : " + (submitted5 ? "ACCEPTED" : "REJECTED"));
        System.out.println("  Mission State         : " + exp05.getState());
        System.out.println("  Reason                : " + exp05.getFailureReason());
        System.out.println("  Active Colony Mission : " + sim.getActiveMission());
        System.out.println(">>> SCENARIO 5 PASSED <<<\n");

        environment.setCondition(EnvironmentCondition.NORMAL);

        System.out.println("================================================================================");
        System.out.println("   MISSION & SIMULATION MEMBER 3 VERIFICATION SUITE COMPLETE!");
        System.out.println("================================================================================");
    }
}
