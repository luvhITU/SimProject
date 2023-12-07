import Places.Hole;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.NonBlocking;

import java.awt.*;

public class WolfBurrow extends Home implements NonBlocking, DynamicDisplayInformationProvider, Hole {
    public WolfBurrow() {
        super(5);
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, "hole");
    }
}
