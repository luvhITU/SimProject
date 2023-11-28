import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.Random;
import java.util.Set;

public class Rabbit extends Animal implements Actor, DynamicDisplayInformationProvider {

    private static final int FIND_HOME_TRESHOLD = 8;
    Hole home;

    public Rabbit() {
        super(Config.Rabbit.DIET, Config.Rabbit.NUTRITION, Config.Rabbit.DAMAGE, Config.Rabbit.ABSORPTION_PERCENTAGE);
        home = null;
    }

    @Override
    public void act(World w) {
        if(getDeleted()) {return;}
        System.out.println("HEEEEEY");
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
                } else if (!neighbours.isEmpty()) {
                    moveToHome(neighbours);
                }
            }
        } else if (w.getCurrentTime() == 0) {
            emerge();
        }
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.blue, "rabbit-large");
    }

    public void randomMove(Set<Location> neighbours) {
        try {
            Location l = (Location) neighbours.toArray()[new Random().nextInt(neighbours.size())];
            getWorld().move(this, l);
        } catch (IllegalArgumentException e) {
            System.out.println(this + " was deleted by another process before it be moved");
        }
    }

    public void tryToEat() {
        Location l = getWorld().getCurrentLocation();
        if (!getWorld().containsNonBlocking(l)) {
            return;
        }
        Object nonBlocking = getWorld().getNonBlocking(l);
        if (getDiet().contains(nonBlocking.getClass().getSimpleName())) {
            Edible edibleObject = (Edible) nonBlocking;
            eat(edibleObject);
        }
    }

    public void digHole() {
        Location curr = getWorld().getCurrentLocation();
        if (getWorld().containsNonBlocking(curr) && !(getWorld().getNonBlocking(curr) instanceof Hole)) {
            //System.out.println("Standing on Non-blocking Object - deleting & digging my home!");
            getWorld().delete(getWorld().getNonBlocking(curr));
        }
        //System.out.println("It's empty here - digging my home!");
        setHome(curr);
    }

    public void setHome(Location l) {
        home = new Hole();
        //System.out.println("Digging hole!");
        getWorld().setTile(l, home);
    }


    public void hide() {
        getWorld().remove(this);
        sleep();
    }

    public void emerge() {
        awaken();
        getWorld().setTile(getWorld().getLocation(home), this);
    }

    public void moveToHome(Set<Location> neighbours) {
        try {
            Location currL = getWorld().getCurrentLocation();
            Location homeL = getWorld().getLocation(home);
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
                getWorld().move(this, bestMove);
            }
        } catch (IllegalArgumentException ignore) {
        }


    }
}