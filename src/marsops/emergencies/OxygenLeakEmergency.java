package marsops.emergencies;

import marsops.Agents.Agent;
import marsops.AppEnums.*;
import marsops.Facilities.Habitat;

/**
 * An oxygen leak in a habitat that must be repaired before it worsens.
 * Requires an Engineer (EQUIPMENT_REPAIR) to seal it.
 */
public class OxygenLeakEmergency extends Emergency {

    private final Habitat affectedHabitat;
    private final int leakSeverity;          // oxygen units lost per event

    public OxygenLeakEmergency(String id, Habitat affectedHabitat, int leakSeverity) {
        super(id, "Oxygen Leak in " + affectedHabitat.getName() + " (severity=" + leakSeverity + ")");
        this.affectedHabitat = affectedHabitat;
        this.leakSeverity    = leakSeverity;

        // Resources needed to seal the breach
        requiredResources.put(ResourceType.SPARE_PARTS,   2);
        requiredResources.put(ResourceType.BATTERY_POWER, 5);
    }

    // ── Capability check ────────────────────────────────────────────────────

    @Override
    public boolean canBeHandledBy(Agent agent) {
        // Need someone who can do equipment repair
        return agent != null && agent.hasCapability(Capability.EQUIPMENT_REPAIR);
    }

    // ── Habitat effects ──────────────────────────────────────────────────────

    /**
     * Apply the initial leak: deducts oxygen from the affected habitat immediately
     * when the emergency is triggered.
     */
    public void applyInitialLeak() {
        affectedHabitat.deductResourceUpTo(ResourceType.OXYGEN, leakSeverity);
    }

    /**
     * Apply a further penalty if the emergency goes unresolved (responders unavailable
     * or resources insufficient).
     */
    public void applyUnresolvedPenalty() {
        affectedHabitat.deductResourceUpTo(ResourceType.OXYGEN, leakSeverity);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public Habitat getAffectedHabitat() { return affectedHabitat; }
    public int getLeakSeverity()        { return leakSeverity; }
}
