package homes;

import animals.Animal;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;

public class Burrow extends Home implements DynamicDisplayInformationProvider, NonBlocking {
    String imageKey;
    public Burrow(Location location, int maxOccupants, String allowedSpecies) {
        super(location, maxOccupants, allowedSpecies);
        imageKey = "hole-small";
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, imageKey);
    }

    @Override
    public void add(Animal animal) {
        super.add(animal);
        if (animal.maxHealth > 70) {
            imageKey = "hole";
        }
    }
}
