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

    public Pack() {
        members = new HashSet<>();
        target = null;
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.cyan);
    }

    public boolean isFull() { return packHome.isFull(); }

    public void add(World w, PackAnimal animal) {
        if (members.contains(animal)) {
            throw new IllegalArgumentException(animal + " is already a member.");
        }
        members.add(animal);
        if (packHome != null) {
            animal.setHome(w, packHome);
        }
    }

    public void remove(PackAnimal animal) {
        if (!members.contains(animal)) {
            throw new IllegalArgumentException(animal + " is not a member.");
        }
        members.remove(animal);
    }

    public Object getTarget() {
        return target;
    }

    public Set<PackAnimal> getMembers() {
        return members;
    }

    public Home getPackHome() {
        return packHome;
    }

    public void setPackHome(World w, Home home) {
        packHome = home;
        for (PackAnimal member : members) {
            member.setHome(w, home);
        }
    }

    public void setTarget(Object target) {
        this.target = target;
    }

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

    public Set<Location> getMemberLocations(World w) {
        Set<Location> memberLocations = new HashSet<>();
        for (PackAnimal member : members) {
            if (w.isOnTile(member)) {
                memberLocations.add(w.getLocation(member));
            }
        }
        return memberLocations;
    }

    public int calcTotalMissingSatiation() {
        int totalMissingNutrition = 0;
        for (PackAnimal member : members) {
            totalMissingNutrition += Animal.getMaxSatiation() - member.getSatiation();
        }
        return totalMissingNutrition;
    }
}
