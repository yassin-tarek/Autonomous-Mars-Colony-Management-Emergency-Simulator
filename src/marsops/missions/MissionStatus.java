package marsops.missions;

import marsops.AppEnums.MissionState;

/**
 * Convenience enum matching MissionState for compatibility.
 */
public enum MissionStatus {
    ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    FAILED;

    public MissionState toState() {
        return MissionState.valueOf(this.name());
    }

    public static MissionStatus fromState(MissionState state) {
        if (state == null) return null;
        return MissionStatus.valueOf(state.name());
    }
}
