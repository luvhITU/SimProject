package ediblesandflora.edibles;

import utils.Config;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;

import java.awt.*;

public class BerryBush extends Edible implements DynamicDisplayInformationProvider {
    /***
     * Constructor that uses configs
     */
    public BerryBush() {
        super(Config.BerryBush.NUTRITION, Config.BerryBush.renewTimeDays);
    }

    /***
     * See super
     * @return  DisplayInformation
     */
    @Override
    public DisplayInformation getInformation() {
            if (isEdible()) {
            return new DisplayInformation(Color.red, "bush-berries");
        } else {
            return new DisplayInformation(Color.green, "bush");
        }
    }
}