import java.awt.Color;
import java.util.*;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.Location;

public class Rabbit extends Animal implements Actor, DynamicDisplayInformationProvider {

    int FIND_HOME_TRESHOLD = 8;
    Burrow home;

    public Rabbit(Program p) {
        super(p, new HashSet<String>(Set.of("Grass")), 1.1);
        home = null;
    }

    @Override
    public void act(World w) {
        super.act(w);
        if (isAwake) {
            tryToEat();
            Set<Location> neighbours = w.getEmptySurroundingTiles();
            if (!neighbours.isEmpty() && w.getCurrentTime() < FIND_HOME_TRESHOLD) {
                randomMove();
            }
            else if (home == null && w.getCurrentTime() == FIND_HOME_TRESHOLD) {
                digHole();
            }
            else if (w.getCurrentLocation() == w.getLocation(home)) {
                hide();
            }
    }

    @Override
    public DisplayInformation getInformation() {
        if (!getIsAwake()) {
            return new DisplayInformation(Color.blue, "rabbit-sleeping");
        } else {
            return new DisplayInformation(Color.gray, "rabbit-large");
        }
    }

    public void randomMove(Set<Location> neighbours){
        try {
            Location l = (Location) neighbours.toArray()[new Random().nextInt(neighbours.size())];
            w.move(this, l);
        } catch(IllegalArgumentException e) {
            System.out.println(this + " was deleted by another process before it be moved");
        }
    }

    public void tryToEat() {
        Location l = w.getCurrentLocation();
        if (!w.containsNonBlocking(l)) {
            return;
        }
        Object nonBlocking = w.getNonBlocking(l);
        if (getFoodSources().contains(nonBlocking.getClass().getSimpleName())) {
            Edible edibleObject = (Edible) nonBlocking;
            eat(edibleObject);
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
}

