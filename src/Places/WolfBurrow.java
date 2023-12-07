package Places;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.NonBlocking;

import java.awt.*;

public class WolfBurrow extends Home implements NonBlocking, DynamicDisplayInformationProvider, Hole {
    /***
     * Uses super of Home where maxAllowed is set to 5
     */
    public WolfBurrow() {
        super(5);
    }

    /***
     * See Super
     * @return  DisplayInformation
     */
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, "hole");
    }
}
