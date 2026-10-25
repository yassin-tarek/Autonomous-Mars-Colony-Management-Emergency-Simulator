package marsops.colony;
import java.util.Optional;
import marsops.AppEnums.EnvironmentCondition;
import marsops.AppEnums.OperationType;

public class MarsEnvironment {
      private EnvironmentCondition condition = EnvironmentCondition.NORMAL;

    public EnvironmentCondition getCondition() { return condition; }

    public void setCondition(EnvironmentCondition condition) { this.condition = condition; }

    public Optional<String> checkRestriction(OperationType operation) {
        return condition.restrictionFor(operation); //safely returns null if no restriction
    }
}
