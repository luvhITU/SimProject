import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.Set;

public class Rabbit extends Animal implements Actor, DynamicDisplayInformationProvider {

    private static final int FIND_HOME_THRESHOLD = 8;
    Hole home;

    public Rabbit() {
        super(Config.Rabbit.DIET, Config.Rabbit.NUTRITION, Config.Rabbit.DAMAGE, Config.Rabbit.ABSORPTION_PERCENTAGE);
        home = null;
    }

    @Override
    public void act(World w) {
        if (getIsDead(w)) {return;}
        System.out.println("HEEEEEY");
        super.act(w);
        if (getIsAwake()) {
            tryToEat(w);
            if (w.getCurrentTime() < FIND_HOME_THRESHOLD) {
                try {
                    randomMove(w);
                } catch(IllegalStateException ignore) {}

            } else if (w.getCurrentTime() >= FIND_HOME_THRESHOLD) {
                if (home == null) {
                    try {
                        createHome(w);
                    } catch (IllegalStateException e) {randomMove(w);}

                } else if (w.getCurrentLocation().equals(w.getLocation(home))) {
                    hide(w);
                }
                moveToHome(w);
            }
        } else if (w.getCurrentTime() == 0) {
            emerge(w);
        }
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.blue, "rabbit-large");
    }

    public void randomMove(World w) {
        Set<Location> neighbours = w.getEmptySurroundingTiles();
        if (neighbours.isEmpty()) {throw new IllegalStateException("No empty tiles to move to");}
        Location l = (Location) neighbours.toArray()[HelperMethods.getRandom().nextInt(neighbours.size())];
        w.move(this, l);
    }

    public void tryToEat(World w) {
        Location l = w.getCurrentLocation();
        if (!w.containsNonBlocking(l)) {
            return;
        }
        Object nonBlocking = w.getNonBlocking(l);
        if (getDiet().contains(nonBlocking.getClass().getSimpleName())) {
            Edible edible = (Edible) nonBlocking;
            eat(w, edible);
        }
    }

    public void createHome(World w) {
        Location l = w.getCurrentLocation();
        boolean containsNonBlocking = w.containsNonBlocking(l);
        Object nonBlocking = null;
        if (containsNonBlocking) {
            nonBlocking = w.getNonBlocking(l);
        }
        if (nonBlocking instanceof Home) {
            throw new IllegalStateException("There already exists a home at this location");
        }
        if (nonBlocking != null) {;
        w.delete(nonBlocking);
        }
        setHome(w, l);
    }

    public void setHome(World w, Location l) {
        home = new Hole();
        //System.out.println("Digging hole!");
        w.setTile(l, home);
    }


    public void hide(World w) {
        w.remove(this);
        sleep();
    }

    public void emerge(World w) {
        awaken();
        w.setTile(w.getLocation(home), this);
    }

    public void moveToHome(World w) {
        Set<Location> neighbours = w.getEmptySurroundingTiles();
        if (neighbours.isEmpty()) {throw new IllegalStateException("No empty tiles to move to");}

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
        } catch (IllegalArgumentException ignore) {
        }


    }
}