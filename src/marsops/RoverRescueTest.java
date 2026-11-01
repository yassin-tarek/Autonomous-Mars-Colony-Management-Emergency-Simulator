package marsops;

import marsops.Agents.Robots.ExplorationRover;
import marsops.AppEnums.*;
import marsops.colony.Colony;
import marsops.emergencies.RoverBreakdownEmergency;
import marsops.missions.Mission;
import marsops.missions.RescueMission;

public class RoverRescueTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void check(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + description);
        } else {
            failed++;
            System.out.println("  [FAIL] " + description);
        }
    }

    public static void main(String[] args) {
        Colony colony = new Colony(); // <-- adjust to your Colony constructor

        ExplorationRover rover = new ExplorationRover("R-01", "Pathfinder", "Crater Rim", 80);

        System.out.println("=== 1. Initial state ===");
        check("Rover starts OPERATIONAL", rover.getCondition() == RobotCondition.OPERATIONAL);

        // ── Breakdown ────────────────────────────────────────────────────────
        System.out.println("\n=== 2. Rover breakdown ===");
        RoverBreakdownEmergency emergency = new RoverBreakdownEmergency("E-01", rover);
        check("Emergency starts TRIGGERED", emergency.getState() == EmergencyState.TRIGGERED);

        emergency.handle(colony);

        check("Rover is DAMAGED after breakdown", rover.getCondition() == RobotCondition.DAMAGED);
        check("Emergency is UNRESOLVED after handle()", emergency.getState() == EmergencyState.UNRESOLVED);
        check("checkResolution() is false before any rescue", !emergency.checkResolution());

        // ── Rescue mission creation ──────────────────────────────────────────
        System.out.println("\n=== 3. Rescue mission creation ===");
        RescueMission rescue = emergency.createRescueMission("M-RESCUE-01", 3);

        check("Rescue mission targets the broken rover", rescue.getRescueLocation().equals(rover.getLocation()));
        check("Rescue location matches the rover location", rescue.getRescueLocation().equals("Crater Rim"));
        check("Rescue mission type is RESCUE", rescue.getType() == MissionType.RESCUE);
        check("Rescue mission starts ASSIGNED", rescue.getState() == MissionState.ASSIGNED);
        check("Emergency exposes the rescue mission", rescue != null);
        check("Broken rover cannot perform its own rescue", !rescue.canBePerformedBy(rover));

        // ── Execution ────────────────────────────────────────────────────────
        System.out.println("\n=== 4. Mission execution ===");
        rescue.setState(MissionState.IN_PROGRESS);

        rescue.advanceTime(1);
        rescue.advanceTime(1);
        check("Mission still IN_PROGRESS before duration is reached",
                rescue.getState() == MissionState.IN_PROGRESS);
        check("Rover still DAMAGED mid-mission", rover.getCondition() == RobotCondition.DAMAGED);
        check("Emergency not resolved mid-mission", !emergency.checkResolution());

        rescue.advanceTime(1); // elapsed 3/3 -> complete()

        check("Mission COMPLETED after full duration", rescue.getState() == MissionState.COMPLETED);
        check("Rover repaired to OPERATIONAL", rover.getCondition() == RobotCondition.OPERATIONAL);
        check("Rover is available again", rover.isAvailable());

        // ── Resolution ───────────────────────────────────────────────────────
        System.out.println("\n=== 5. Emergency resolution ===");
        check("checkResolution() returns true", emergency.checkResolution());
        check("Emergency is RESOLVED", emergency.getState() == EmergencyState.RESOLVED);
        System.out.println("  Note: " + emergency.getResolutionNote());

        // ── Failed rescue ────────────────────────────────────────────────────
        System.out.println("\n=== 6. Failed rescue leaves the emergency unresolved ===");
        ExplorationRover rover2 = new ExplorationRover("R-02", "Opportunity", "Dune Field", 60);
        RoverBreakdownEmergency emergency2 = new RoverBreakdownEmergency("E-02", rover2);
        emergency2.handle(colony);

        RescueMission rescue2 = emergency2.createRescueMission("M-RESCUE-02", 3);
        rescue2.setState(MissionState.IN_PROGRESS);
        rescue2.fail("Communication disruption");

        check("Failed mission state is FAILED", rescue2.getState() == MissionState.FAILED);
        check("Rover stays DAMAGED after failed rescue", rover2.getCondition() == RobotCondition.DAMAGED);
        check("Emergency stays UNRESOLVED", !emergency2.checkResolution()
                && emergency2.getState() == EmergencyState.UNRESOLVED);

        // ── Active mission failure on breakdown ──────────────────────────────
        System.out.println("\n=== 7. Breakdown fails an active mission using the rover ===");
        ExplorationRover rover3 = new ExplorationRover("R-03", "Spirit", "Plains", 70);
        RoverBreakdownEmergency surveySetup = new RoverBreakdownEmergency("E-SURVEY-SETUP", rover3);
        Mission survey = surveySetup.createRescueMission("M-SURVEY", 5);
        survey.assignAgent(rover3);
        survey.setState(MissionState.IN_PROGRESS);
        colony.setActiveMission(survey);

        new RoverBreakdownEmergency("E-03", rover3).handle(colony);

        check("Active mission FAILED when its rover breaks down", survey.getState() == MissionState.FAILED);
        check("Colony active mission cleared", colony.getActiveMission() == null);
        check("Failure reason mentions the rover", survey.getFailureReason().contains("Spirit"));

        // ── Summary ──────────────────────────────────────────────────────────
        System.out.println("\n=== Summary: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) {
            System.exit(1);
        }
    }
}