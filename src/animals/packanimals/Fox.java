package animals.packanimals;

import animals.Animal;
import ediblesandflora.edibles.Carcass;
import ediblesandflora.edibles.Edible;
import homes.Burrow;
import itumulator.world.Location;
import utils.Config;
import itumulator.world.World;
import utils.HelperMethods;

import java.util.HashSet;
import java.util.Set;

public class Fox extends PackAnimal {
    /***
     * Constructor that uses configs
     */
    public Fox() {
        super(Config.Fox.DIET, Config.Fox.DAMAGE, Config.Fox.HEALTH, Config.Fox.SPEED, Config.Fox.MATING_COOLDOWN_DAYS, Config.Fox.MAX_PACK_SIZE, true, true);
    }

    /***
     * See super
     * @param w World
     */
    @Override
    public void sleepAct(World w) {
        if (w.getCurrentTime() == 10 && canMate() && home != null) {
            burrowMatingPackage(w);
        }
        super.sleepAct(w);
    }

    /***
     * See super
     * @param w World
     * @return  Pray
     */
    @Override
    public Object findTarget(World w) {
        Edible edible = findClosestEdible(w);
        Animal prey = findClosestPrey(w);
        Burrow burrow = findClosestBurrow(w);
        if (edible == null && prey == null && burrow == null) {
            return null;
        } else if (burrow != null && home == null) {
            return burrow;
        } else if (!(edible instanceof Carcass) && prey != null) {
            return prey;
        } else if (prey == null) {
            return edible;
        }
        if (burrow == null) {
            return null;
        }
        int distEdible = HelperMethods.getDistance(w.getLocation(this), w.getLocation(edible));
        int distPrey = HelperMethods.getDistance(w.getLocation(this), w.getLocation(prey));
        int distBurrow = HelperMethods.getDistance(w.getLocation(this), w.getLocation(burrow));

        if (distBurrow < distEdible && distBurrow < distPrey && home == null) {
            return burrow;
        } else if (distEdible < distPrey) {
            return edible;
        } else {
            return prey;
        }
    }

    private Burrow findClosestBurrow(World w) {
        return (Burrow) HelperMethods.findNearestOfObjects(w, w.getLocation(this), findBurrow(w));
    }

    private Set<Burrow> findBurrow(World w) {
        Set<Burrow> burrows = new HashSet<>();
        for (Location l : tilesInSight) {
            Object o = w.getTile(l);
            if (o instanceof Burrow && ((Burrow) o).getAllowedSpecies().equals("Rabbit")) {
                burrows.add((Burrow) o);
            }
        }
        return burrows;
    }
}



