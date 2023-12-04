import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.NonBlocking;

import java.awt.*;

public class RabbitBurrow extends Home implements NonBlocking, DynamicDisplayInformationProvider, Hole {
    public RabbitBurrow() {
        super(5);
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, "hole-small");
    }
}
