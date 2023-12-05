import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;

import java.awt.*;

public class Carcass extends Edible implements DynamicDisplayInformationProvider, Meat {
    private final String carcassType;

    public Carcass(AnimalOLD animal) {
        super(animal.getMaxHealth());
        this.carcassType = animal.getType();
    }

    @Override
    public DisplayInformation getInformation() {
        String imageKey = getNutrition() > 100 ? "carcass" : "carcass-small";
        return new DisplayInformation(Color.magenta, imageKey);
    }

    @Override
    public String getType() { return carcassType; }
}
