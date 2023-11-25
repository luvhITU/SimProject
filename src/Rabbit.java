import java.awt.Color;
import java.util.*;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.Location;

public class Rabbit extends Animal implements Actor, DynamicDisplayInformationProvider {

    Burrow home = null;
    boolean hasHome;
    int findHomeThreshold = 8;
    boolean isHiding = false;

    public Rabbit(Program p) {
        super(p, new HashSet<String>(Set.of("Grass")), 1.1);
    }

    @Override
    public void act(World w) {
        try {
            super.act(w);

            if (w.isDay()) {
                if (!isHiding && w.getCurrentTime() < findHomeThreshold) {
                    move();
                } else if (isHiding && w.getLocation(this) == w.getLocation(home)) {
                    System.out.println("Is Hiding");
                } else if (w.getCurrentTime() == findHomeThreshold) {
                    digHole();
                } else if (!isHiding && w.getCurrentTime() > findHomeThreshold) {
                    hide();
                }
            }

        } catch (IllegalArgumentException ignored) {}
        tryToEat();
    }

    @Override
    public DisplayInformation getInformation() {
        if (w.isNight() && w.getLocation(this) == w.getLocation(home)) {
            return new DisplayInformation(Color.blue, "rabbit-sleeping");
        } else {
            return new DisplayInformation(Color.gray, "rabbit-large");
        }
    }

    public void move() {
        Set<Location> neighbours = w.getEmptySurroundingTiles();
        Location l = (Location) neighbours.toArray()[new Random().nextInt(neighbours.size())];
        w.move(this, l);
    }

    void tryToEat() {
        try {
            Object nonBlocking = getCurrentNonBlocking();
            if (getFoodSources().contains(nonBlocking.getClass().getSimpleName())) {
                Edible EdibleObject = (Edible) nonBlocking;
                setSatiation(Math.max(EdibleObject.getNutrition(), getMaxSatiation()));
                w.delete(nonBlocking);
            }
        } catch (IllegalArgumentException e) {
            return;
        }
    }

    public void digHole() {
        Location curr = w.getCurrentLocation();
        if (!hasHome) {
            if (w.containsNonBlocking(curr) && !(w.getNonBlocking(curr) instanceof Burrow)) {
                //System.out.println("Standing on Non-blocking Object - deleting & digging my home!");
                w.delete(w.getNonBlocking(curr));
            }
            //System.out.println("It's empty here - digging my home!");
            setHome(curr);
        } else if (w.getLocation(this) != w.getLocation(home)) {
            //System.out.println("I have a home - moving to home: " + w.getLocation(home));
            w.move(this, w.getLocation(home));
        }
    }

    public void setHome(Location l) {
        home = new Burrow(p);
        //System.out.println("Digging hole!");
        w.setTile(l, home);
        hasHome = true;
    }


    public void hide() {
        Location curr = w.getCurrentLocation();
        if (curr == w.getLocation(home)) {
            w.remove(this);
            isHiding = true;
        }
    }
}

