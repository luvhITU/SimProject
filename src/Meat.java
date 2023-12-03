import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;

import java.awt.*;

public class Meat extends Edible implements DynamicDisplayInformationProvider {

    public Meat(int nutrition) {
        super(nutrition);
    }

    @Override
    public DisplayInformation getInformation() {
        String imageKey = (getNutrition() > 100) ? "carcass" : "carcass-small";
        return new DisplayInformation(Color.magenta, imageKey);
    }
}
