package marsops;

import java.util.List;

import marsops.Agents.Agent;
import marsops.Agents.Crew.Scientist;
import marsops.Agents.Crew.MedicalOfficer;
import marsops.Agents.Crew.Engineer;
import marsops.Agents.Robots.ExplorationRover;
import marsops.Agents.Robots.RepairRobot;
import marsops.AppEnums.*;
import marsops.Facilities.Laboratory;
import marsops.Facilities.Habitat;
import marsops.colony.Colony;
import marsops.missions.ExplorationMission;
import marsops.missions.SimulationManager;
import marsops.emergencies.OxygenLeakEmergency;

public class Main {

    public static void main(String[] args) {

        boolean[] results = new boolean[5];

        System.out.println("\n========================================");
        System.out.println("       MARS-OPS TEST SCENARIOS");
        System.out.println("========================================");

        results[0] = scenario1();
        results[1] = scenario2();
        results[2] = scenario3();
        results[3] = scenario4();
        results[4] = scenario5();

        System.out.println("\n========================================");
        System.out.println("             FINAL RESULTS");
        System.out.println("========================================");

        int passed = 0;

        for (int i = 0; i < results.length; i++) {
            System.out.println("Scenario " + (i + 1) + ": "
                    + (results[i] ? "PASSED" : "FAILED"));

            if (results[i]) {
                passed++;
            }
        }

        System.out.println("----------------------------------------");
        System.out.println("Total: " + passed + "/5 scenarios passed");
        System.out.println("========================================");
    }

    private static boolean scenario1() {

        System.out.println("\n===== SCENARIO 1: SUCCESSFUL EXPLORATION =====");

        Colony colony = new Colony("Ares Colony Alpha");

        Laboratory lab = new Laboratory(
                "Research Lab", "Habitat Zone", 10
        );

        lab.addResource(ResourceType.BATTERY_POWER, 20);
        lab.addResource(ResourceType.FUEL, 10);
        colony.addFacility(lab);

        Scientist scientist = new Scientist(
                "S-01", "Test Scientist", "Research Lab"
        );

        ExplorationRover rover = new ExplorationRover(
                "R-01", "Mars Rover", "Research Lab", 100
        );

        colony.addAgent(scientist);
        colony.addAgent(rover);

        SimulationManager manager = new SimulationManager(colony);

        ExplorationMission mission = new ExplorationMission(
                "M-01", "Mars Surface Exploration", 3, "Valles Marineris"
        );

        boolean accepted = manager.submitMission(
                mission, List.of(scientist, rover)
        );

        manager.advanceTime(3);

        System.out.println("Mission state: " + mission.getState());
        System.out.println("Scientist available: " + scientist.isAvailable());
        System.out.println("Rover available: " + rover.isAvailable());
        System.out.println("Rover battery: " + rover.getBatteryLevel());

        boolean passed = accepted
                && mission.getState() == MissionState.COMPLETED
                && scientist.isAvailable()
                && rover.isAvailable()
                && manager.getActiveMission() == null;

        System.out.println(passed
                ? "SCENARIO 1 PASSED!"
                : "SCENARIO 1 FAILED!");

        return passed;
    }

    private static boolean scenario2() {

        System.out.println("\n===== SCENARIO 2: MISSION REJECTION =====");

        Colony colony = new Colony("Ares Colony Alpha");

        Laboratory lab = new Laboratory(
                "Research Lab", "Habitat Zone", 10
        );

        lab.addResource(ResourceType.BATTERY_POWER, 20);
        lab.addResource(ResourceType.FUEL, 10);
        colony.addFacility(lab);

        MedicalOfficer officer = new MedicalOfficer(
                "MO-01", "Medical Officer", "Research Lab"
        );

        colony.addAgent(officer);

        SimulationManager manager = new SimulationManager(colony);

        ExplorationMission mission = new ExplorationMission(
                "M-02", "Mars Surface Exploration", 3, "Valles Marineris"
        );

        boolean accepted = manager.submitMission(
                mission, List.of(officer)
        );

        System.out.println("Mission accepted: " + accepted);
        System.out.println("Mission state: " + mission.getState());
        System.out.println("Failure reason: " + mission.getFailureReason());
        System.out.println("Officer available: " + officer.isAvailable());

        boolean passed = !accepted
                && mission.getState() == MissionState.FAILED
                && officer.isAvailable()
                && manager.getActiveMission() == null;

        System.out.println(passed
                ? "SCENARIO 2 PASSED!"
                : "SCENARIO 2 FAILED!");

        return passed;
    }

    private static boolean scenario3() {

        System.out.println("\n===== SCENARIO 3: RESOURCE SHORTAGE =====");

        Colony colony = new Colony("Ares Colony Alpha");

        Laboratory lab = new Laboratory(
                "Research Lab", "Habitat Zone", 10
        );

        lab.addResource(ResourceType.BATTERY_POWER, 5);
        lab.addResource(ResourceType.FUEL, 10);
        colony.addFacility(lab);

        Scientist scientist = new Scientist(
                "S-03", "Test Scientist", "Research Lab"
        );

        ExplorationRover rover = new ExplorationRover(
                "R-03", "Mars Rover", "Research Lab", 100
        );

        colony.addAgent(scientist);
        colony.addAgent(rover);

        SimulationManager manager = new SimulationManager(colony);

        ExplorationMission mission = new ExplorationMission(
                "M-03", "Mars Surface Exploration", 3, "Valles Marineris"
        );

        boolean accepted = manager.submitMission(
                mission, List.of(scientist, rover)
        );

        System.out.println("Mission accepted: " + accepted);
        System.out.println("Mission state: " + mission.getState());
        System.out.println("Failure reason: " + mission.getFailureReason());
        System.out.println("Scientist available: " + scientist.isAvailable());
        System.out.println("Rover available: " + rover.isAvailable());
        System.out.println("Rover battery: " + rover.getBatteryLevel());
        System.out.println(manager.getResourceManager().describeTotals());

        boolean passed = !accepted
                && mission.getState() == MissionState.FAILED
                && scientist.isAvailable()
                && rover.isAvailable()
                && rover.getBatteryLevel() == 100
                && manager.getActiveMission() == null
                && lab.getResourceQuantity(ResourceType.BATTERY_POWER) == 5
                && lab.getResourceQuantity(ResourceType.FUEL) == 10;

        System.out.println(passed
                ? "SCENARIO 3 PASSED!"
                : "SCENARIO 3 FAILED!");

        return passed;
    }

    private static boolean scenario4() {

        System.out.println("\n===== SCENARIO 4: OXYGEN LEAK EMERGENCY =====");

        Colony colony = new Colony("Ares Colony Alpha");

        Habitat habitat = new Habitat(
                "Main Habitat", "Habitat Zone", 10
        );

        habitat.addResource(ResourceType.OXYGEN, 100);
        habitat.addResource(ResourceType.SPARE_PARTS, 10);
        habitat.addResource(ResourceType.BATTERY_POWER, 10);

        colony.addFacility(habitat);

        Engineer engineer = new Engineer(
                "E-01", "Test Engineer", "Main Habitat"
        );

        RepairRobot robot = new RepairRobot(
                "RR-01", "Repair Robot", "Main Habitat", 100
        );

        colony.addAgent(engineer);
        colony.addAgent(robot);

        OxygenLeakEmergency emergency = new OxygenLeakEmergency(
                "EM-01", habitat, 20
        );

        emergency.handle(colony);

        System.out.println("Emergency state: " + emergency.getState());
        System.out.println("Oxygen remaining: "
                + habitat.getResourceQuantity(ResourceType.OXYGEN));
        System.out.println("Life support functional: "
                + habitat.getLifeSupport().isFunctional());
        System.out.println("Habitat operational: " + habitat.isOperational());
        System.out.println("Engineer available: " + engineer.isAvailable());
        System.out.println("Repair robot available: " + robot.isAvailable());

        boolean passed = emergency.getState() == EmergencyState.RESOLVED
                && habitat.getResourceQuantity(ResourceType.OXYGEN) == 80
                && habitat.getLifeSupport().isFunctional()
                && habitat.isOperational()
                && engineer.isAvailable()
                && robot.isAvailable();

        System.out.println(passed
                ? "SCENARIO 4 PASSED!"
                : "SCENARIO 4 FAILED!");

        return passed;
    }

    private static boolean scenario5() {

        System.out.println("\n===== SCENARIO 5: DUST STORM =====");

        Colony colony = new Colony("Ares Colony Alpha");

        colony.getEnvironment().setCondition(
                EnvironmentCondition.DUST_STORM
        );

        Laboratory lab = new Laboratory(
                "Research Lab", "Habitat Zone", 10
        );

        lab.addResource(ResourceType.BATTERY_POWER, 20);
        lab.addResource(ResourceType.FUEL, 10);
        colony.addFacility(lab);

        Scientist scientist = new Scientist(
                "S-05", "Test Scientist", "Research Lab"
        );

        ExplorationRover rover = new ExplorationRover(
                "R-05", "Mars Rover", "Research Lab", 100
        );

        colony.addAgent(scientist);
        colony.addAgent(rover);

        SimulationManager manager = new SimulationManager(colony);

        ExplorationMission mission = new ExplorationMission(
                "M-05", "Mars Surface Exploration", 3, "Valles Marineris"
        );

        boolean accepted = manager.submitMission(
                mission, List.of(scientist, rover)
        );

        System.out.println("Environment: "
                + colony.getEnvironment().getCondition());
        System.out.println("Mission accepted: " + accepted);
        System.out.println("Mission state: " + mission.getState());
        System.out.println("Failure reason: " + mission.getFailureReason());
        System.out.println("Scientist available: " + scientist.isAvailable());
        System.out.println("Rover available: " + rover.isAvailable());
        System.out.println("Rover battery: " + rover.getBatteryLevel());
        System.out.println(manager.getResourceManager().describeTotals());

        boolean passed = !accepted
                && mission.getState() == MissionState.FAILED
                && scientist.isAvailable()
                && rover.isAvailable()
                && rover.getBatteryLevel() == 100
                && manager.getActiveMission() == null
                && lab.getResourceQuantity(ResourceType.BATTERY_POWER) == 20
                && lab.getResourceQuantity(ResourceType.FUEL) == 10;

        System.out.println(passed
                ? "SCENARIO 5 PASSED!"
                : "SCENARIO 5 FAILED!");

        return passed;
    }
}