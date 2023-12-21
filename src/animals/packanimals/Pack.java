package animals.packanimals;

import animals.Animal;
import homes.Home;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.*;

public class Pack implements DynamicDisplayInformationProvider {
    private final Set<PackAnimal> members;
    private Home packHome;
    private Object target;

    /***
     * Pack constructor
     */
    public Pack() {
        members = new HashSet<>();
        target = null;
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
     * Returns true if there is no space left in pack
     * @return  Boolean
     */
    public boolean isFull() {
        return members.size() == members.iterator().next().maxPackSize;
    }

    /***
     * Adds pack animal to pack and throws and exception if the pack animal already is in the pack
     * @param w         World
     * @param animal    PackAnimal
     */
    public void add(World w, PackAnimal animal) {
        if (members.contains(animal)) {
            throw new IllegalArgumentException(animal + " is already a member.");
        }
        members.add(animal);
        animal.setPack(this);
        if (packHome != null) {
            animal.setHome(w, packHome);
        }
    }

    /***
     * Removes the pack animal from the pack
     * @param animal    PackAnimal
     */
    public void remove(PackAnimal animal) {
        if (!members.contains(animal)) {
            throw new IllegalArgumentException(animal + " is not a member.");
        }
        members.remove(animal);
    }

    /***
     * Getter for the target for the pack
     * @return  Target for the hunt, animal
     */
    public Object getTarget() {
        return target;
    }

    /***
     * Getter for members of the pack in a set
     * @return  Set of pack animals
     */
    public Set<PackAnimal> getMembers() {
        return members;
    }

    /***
     * Getter for Home of the pack
     * @return  Home
     */
    public Home getPackHome() {
        return packHome;
    }

    /***
     * Sets packHome for the pack and all members of the pack
     * @param w     World
     * @param home  Home
     */
    public void setPackHome(World w, Home home) {
        packHome = home;
        for (PackAnimal member : members) {
            member.setHome(w, home);
        }
    }

    /***
     * Sets the target for the pack
     * @param target    Object, animal
     */
    public void setTarget(Object target) {
        this.target = target;
    }

    /***
     * Returns all locations which in sight of members of the pack
     * @param w World
     * @return  Set of locations which is in sight of all members
     */
    public Set<Location> calcPackTilesInSight(World w) {
        Set<Location> packTilesOfSight = new HashSet<>();
        for (PackAnimal member : members) {
            if (w.isOnTile(member)) {
                packTilesOfSight.addAll(member.calcPersonalTilesInSight(w));
            }
        }
        packTilesOfSight.removeAll(getMemberLocations(w));
        return packTilesOfSight;
    }

    /***
     * Returns sets of locations of members of the pack
     * @param w World
     * @return  Set of Locations
     */
    public Set<Location> getMemberLocations(World w) {
        Set<Location> memberLocations = new HashSet<>();
        for (PackAnimal member : members) {
            if (w.isOnTile(member)) {
                memberLocations.add(w.getLocation(member));
            }
        }
        return memberLocations;
    }
}