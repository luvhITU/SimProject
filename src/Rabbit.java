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
        if (getIsAwake()) {
            Set<Location> neighbours = w.getEmptySurroundingTiles();
            if (!neighbours.isEmpty() && w.getCurrentTime() < FIND_HOME_TRESHOLD) {
                randomMove(neighbours);
            } else if (w.getCurrentTime() == FIND_HOME_TRESHOLD) {
                if (home == null) {
                    digHole();
                } else if (w.getCurrentLocation() == w.getLocation(home)) {
                    hide();
                } else if (!neighbours.isEmpty()){
                    moveToHome(neighbours);
                }
                tryToEat();

            } else {
                if (w.getCurrentTime() == 0) {
                    emerge();
                }
            }
        }
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.blue, "rabbit-large");
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
        if (w.containsNonBlocking(curr) && !(w.getNonBlocking(curr) instanceof Burrow)) {
            //System.out.println("Standing on Non-blocking Object - deleting & digging my home!");
            w.delete(w.getNonBlocking(curr));
            //System.out.println("It's empty here - digging my home!");
            setHome(curr);
        } else if (!w.containsNonBlocking(curr)) {
            //System.out.println("It's empty here - digging my home!");
            setHome(curr);
        } else if (w.getCurrentLocation() != w.getLocation(home)) {
            //System.out.println("I have a home - moving to home: " + w.getLocation(home));
            w.move(this, w.getLocation(home));
        }
    }

    public void setHome(Location l) {
        home = new Burrow(p);
        //System.out.println("Digging hole!");
        w.setTile(l, home);
    }


    public void hide() {
            w.remove(this);
            sleep();
    }

    public void emerge() {
        awaken();
        w.setTile(w.getLocation(home), this);
    }

    public void moveToHome(Set<Location> neighbours) {
        Location currL = w.getCurrentLocation();
        Location homeL = w.getLocation(home);
        if (currL.equals(homeL)) {
            return;
        }

        int xDifference = currL.getX() - homeL.getX();
        int yDifference = currL.getY() - homeL.getY();
        int xDirection = 0;
        int yDirection = 0;

        if (xDifference > 0) {
            xDirection = -1;
        } else if(xDifference < 0) {
            xDirection = 1
        }
        if (yDifference > 0) {
            yDirection = -1;
        } else if(yDifference < 0) {
            yDirection = 1
        }
    }
}

