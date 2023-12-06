import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.Set;

public class Bear extends Animal implements Actor {

    private static final int TERRITORY_RADIUS = 3;
    private Location territoryCenter;


    public Bear(Location territoryCenter) {
        super(Config.Bear.DIET, Config.Bear.NUTRITION, Config.Bear.DAMAGE, Config.Bear.HEALTH, Config.Bear.SPEED);
        this.territoryCenter = territoryCenter;
    }

    @Override
    public void act(World w) {
        super.act(w);
        if (w.isNight()) {
            goHome(w);
        } else if (w.getCurrentTime() == 0 && !isAwake) {
            wakeUp(w);
        }
        if (isAwake) {
            Object closestEdible = findClosestEdible(w);
            if (closestEdible == null) {
                wander(w);
            } else {
                hunt(w, closestEdible);
            }

        }
    }

    @Override
    public Location getHomeLocation(World w) {
        return territoryCenter;
    }

    @Override
    public Set<Location> calcTilesInSight(World w) {
        if (satiation >= 50) {
            return w.getSurroundingTiles(territoryCenter, TERRITORY_RADIUS);
        }
        return super.calcTilesInSight(w);
    }

    @Override
    public void randomMove(World w) {
        if (satiation < 50) {
            super.randomMove(w);
            return;
        }

        Set<Location> validLocations = HelperMethods.getEmptySurroundingTiles(w, territoryCenter, TERRITORY_RADIUS);
        if (validLocations.isEmpty()) {
            return;
        }

        Location newLocation = validLocations.toArray(new Location[0])[HelperMethods.getRandom().nextInt(validLocations.size())];
        w.move(this, newLocation);
        actionCost(2);
    }

    private void wander(World w) {
        if (energy < 50) {
            moveToMiddle(w);
        } else {
            randomMove(w);
        }
    }

//    public void tryToEat(World w) {
//        Location l = w.getCurrentLocation();
//        if (!w.containsNonBlocking(l)) {
//            return;
//        }
//        Object nonBlocking = w.getNonBlocking(l);
//        if (diet.contains(nonBlocking.getClass().getSimpleName())) {
//            Edible edible = (Edible) nonBlocking;
//            eat(w, edible);
//        }
//    }
}