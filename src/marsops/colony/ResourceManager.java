package marsops.colony;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import marsops.AppEnums.ResourceType;
import marsops.Facilities.Facility;

public class ResourceManager {
  
    private final List<Facility> facilities;

    public ResourceManager(List<Facility> facilities){
        this.facilities = facilities;
    }

    int getTotalResources(ResourceType type){
       int total = 0;
        for (Facility facility : facilities) {
            if (facility.isOperational()) {
                total += facility.getResourceQuantity(type);
            }
        }
        return total;
    } //total resources per type 

    public Map<ResourceType, Integer> getTotals() {
        Map<ResourceType, Integer> totals = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            totals.put(type, getTotalResources(type));
        }
        return totals;
    } //total resources for all types

    public Optional<String> checkSupply(Map<ResourceType, Integer> request) {
        for (Map.Entry<ResourceType, Integer> entry : request.entrySet()) {
            ResourceType type = entry.getKey();
            int required = entry.getValue();
            if (required < 0) {
                return Optional.of("Invalid request: negative amount of " + type);
            }
            int available = getTotalResources(type);
            if (required > available) {
                return Optional.of("Insufficient " + type + ": required " + required
                        + ", available " + available);
            }
        }
        return Optional.empty();
    }
 
    public Optional<String> consume(Map<ResourceType, Integer> request) {
        Optional<String> failure = checkSupply(request);
        if (failure.isPresent()) {
            return failure;
        }
        for (Map.Entry<ResourceType, Integer> entry : request.entrySet()) {
            int remaining = entry.getValue();
            for (Facility facility : facilities) {
                if (remaining == 0) {
                    break;
                }
                if (facility.isOperational()) {
                    remaining -= facility.deductResourceUpTo(entry.getKey(), remaining);
                }
            }
        }
        return Optional.empty();
    }
 
    public String describeTotals() {
        StringBuilder report = new StringBuilder("Colony resources:\n");
        getTotals().forEach((type, amount) ->
                report.append("  ").append(type).append(": ").append(amount).append("\n"));
        return report.toString();
    }

}

