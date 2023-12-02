import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;

import java.awt.*;

public class BearTerritory implements DynamicDisplayInformationProvider {

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.red, "bear-territory");
    }
}
