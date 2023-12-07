import Helper.Config;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;

import java.awt.*;

public class BerryBush extends RenewableEdible implements Actor, DynamicDisplayInformationProvider {
    public BerryBush() {
        super(Config.BerryBush.NUTRITION, Config.BerryBush.renewTimeDays);
    }

    @Override
    public DisplayInformation getInformation() {
            if (getNutrition() > 0) {
            return new DisplayInformation(Color.red, "bush-berries");
        } else {
            return new DisplayInformation(Color.green, "bush");
        }
    }
}