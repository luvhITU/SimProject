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

    /***
     * Initializes the location, maxOccupants and allowedSpecies.
     * @param location          Location
     * @param maxOccupants      int
     * @param allowedSpecies    String of the desired species
     */
    public Home(Location location, int maxOccupants, String allowedSpecies) {
        this.location = location;
        this.maxOccupants = maxOccupants;
        this.allowedSpecies = allowedSpecies;
        occupants = new HashSet<>();
    }

    /***
     * See super
     * @return  DisplayInformation
     */
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.cyan);
    }

    /***
     * Returns string of the allowed species in the Home
     * @return  String of species
     */
    public String getAllowedSpecies() {
        return allowedSpecies;
    }

    /***
     * Getter for the location of the Home
     * @return  Location
     */
    public Location getLocation() {
        return location;
    }

    /***
     * Getter for the int of limit of occupants in the Home
     * @return  int
     */
    public int getMaxOccupants() { return maxOccupants; }

    /***
     * Add the input animal to the occupants list and throws an exception if there is no room left
     * @param animal
     */

    /***
     * Getter for home occupants
     * @return set of occupants
     */
    public Set<Animal> getOccupants () {
        return occupants;
    }
    public void add(Animal animal) {
        if (isFull()) { throw new IllegalStateException("Home is filled to brim already!"); }
        occupants.add(animal);
    }

    /***
     * Removes input animal from occupants list and throws an exception if the animal doesn't live there
     * @param animal
     */
    public void remove(Animal animal) {
        if (!occupants.contains(animal)) { throw new IllegalArgumentException(animal + " does not live here"); }
        occupants.remove(animal);
    }

    /***
     * Returns true if occupants is at max size
     * @return  Boolean
     */
    public boolean isFull() {
        return occupants.size() == maxOccupants;
    }
}
