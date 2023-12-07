import Helper.Config;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.Set;

public class Bear extends Animal implements Actor {
    private static final int TERRITORY_RADIUS = 2;
    private Location territoryCenter;


    public Bear() {
        super(Config.Bear.DIET, Config.Bear.DAMAGE, Config.Bear.HEALTH, Config.Bear.SPEED, Config.Bear.MATING_COOLDOWN_DAYS);
        territoryCenter = null;
    }

    @Override
    public void act(World w) {
        if (territoryCenter == null) {
            territoryCenter = w.getLocation(this);
        }
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
        if (isDead()) {
            delete(w);
        }
    }

    @Override
    public Location getHomeLocation(World w) {
        return territoryCenter;
    }

    @Override
    public Set<Location> calcTilesInSight(World w) {
        if (satiation >= 50) {
            Set<Location> tilesInSight = w.getSurroundingTiles(territoryCenter, TERRITORY_RADIUS);
            tilesInSight.remove(w.getLocation(this));
            return tilesInSight;
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
        moveTo(w, newLocation);
    }

    private void wander(World w) {
        if (energy < 50) {
            moveToMiddle(w);
        } else {
            randomMove(w);
        }
    }
}