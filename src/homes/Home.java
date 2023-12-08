package homes;

import animals.Animal;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Home implements DynamicDisplayInformationProvider {
    private final Location location;
    private final int maxOccupants;
    private final Set<Animal> occupants;
    private final String allowedSpecies;

    public Home(Location location, int maxOccupants, String allowedSpecies) {
        this.location = location;
        this.maxOccupants = maxOccupants;
        this.allowedSpecies = allowedSpecies;
        occupants = new HashSet<>();
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.cyan);
    }

    public String getAllowedSpecies() {
        return allowedSpecies;
    }

    public Location getLocation() {
        return location;
    }
    public int getMaxOccupants() { return maxOccupants; }
    public void add(Animal animal) {
        if (isFull()) { throw new IllegalStateException("Home is filled to brim already!"); }
        occupants.add(animal);
    }

    public void remove(Animal animal) {
        if (!occupants.contains(animal)) { throw new IllegalArgumentException(animal + " does not live here"); }
        occupants.remove(animal);
    }

    public boolean isFull() {
        return occupants.size() == maxOccupants;
    }
}
