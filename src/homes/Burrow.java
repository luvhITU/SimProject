package homes;

import animals.Animal;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;

public class Burrow extends Home implements DynamicDisplayInformationProvider, NonBlocking {
    private String imageKey;

    /***
     * Initializes using super for location, maxOccupants and allowedSpecies. Sets imageKey to "hole-small"
     * @param location          Location
     * @param maxOccupants      int
     * @param allowedSpecies    String of desired species
     */
    public Burrow(Location location, int maxOccupants, String allowedSpecies) {
        super(location, maxOccupants, allowedSpecies);
        imageKey = "hole-small";
    }

    /***
     * See super
     * @return  DisplayInformation
     */
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, imageKey);
    }

    /***
     * See super
     * @param animal Animal
     */
    @Override
    public void add(Animal animal) {
        super.add(animal);
        if (animal.getMaxHealth() > 70) {
            imageKey = "hole";
        }
    }
}
