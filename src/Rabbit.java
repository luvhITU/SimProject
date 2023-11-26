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
    Hole home;

    public Rabbit(Program p) {
        super(p, new HashSet<String>(Set.of("Grass")), 1.1);
        home = null;
    }

    @Override
    public void act(World w) {
        super.act(w);
        if (getIsAwake()) {
            tryToEat();
            Set<Location> neighbours = w.getEmptySurroundingTiles();
            if (!neighbours.isEmpty() && w.getCurrentTime() < FIND_HOME_TRESHOLD) {
                randomMove(neighbours);
            } else if (w.getCurrentTime() >= FIND_HOME_TRESHOLD) {
                if (home == null) {
                    digHole();
                } else if (w.getCurrentLocation().equals(w.getLocation(home))) {
                    hide();
                } else if (!neighbours.isEmpty()){
                    moveToHome(neighbours);
                }
            }
        } else if (w.getCurrentTime() == 0) {emerge();}
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
        if (w.containsNonBlocking(curr) && !(w.getNonBlocking(curr) instanceof Hole)) {
            //System.out.println("Standing on Non-blocking Object - deleting & digging my home!");
            w.delete(w.getNonBlocking(curr));
        }
        //System.out.println("It's empty here - digging my home!");
        setHome(curr);
    }

    public void setHome(Location l) {
        home = new Hole(p);
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
        try {
            Location currL = w.getCurrentLocation();
            Location homeL = w.getLocation(home);
            if (currL.equals(homeL)) {
                return;
            }
            int minDistance = Integer.MAX_VALUE;
            Location bestMove = null;
            for (Location n : neighbours) {
                int nDistance = Math.abs(n.getX() - homeL.getX()) + Math.abs((n.getY() - homeL.getY()));
                if (nDistance < minDistance) {
                    minDistance = nDistance;
                    bestMove = n;

                }
            }
            if (bestMove != null) {
                w.move(this, bestMove);
            }
        } catch(IllegalArgumentException ignore) {return;}




    }
}