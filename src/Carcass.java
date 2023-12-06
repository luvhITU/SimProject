import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;

import java.awt.*;

public class Carcass extends Edible implements DynamicDisplayInformationProvider {
    private final String carcassType;

    public Carcass(Animal animal) {
        super(animal.maxHealth);
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
