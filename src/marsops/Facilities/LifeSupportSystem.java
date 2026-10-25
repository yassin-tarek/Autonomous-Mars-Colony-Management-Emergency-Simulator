package marsops.Facilities;

public class LifeSupportSystem {
  private boolean functional;
    private int filtrationEfficiency;

    public LifeSupportSystem() {
        this.functional = true;
        this.filtrationEfficiency = 100;
    }

    public boolean isFunctional() { return functional; }
    public void setFunctional(boolean functional) {
        this.functional = functional;
        this.filtrationEfficiency = functional ? 100 : 40;//in case oxygen leak it will drop to 40
    }
    public int getFiltrationEfficiency() { return filtrationEfficiency; }//effeciency getter 

}
