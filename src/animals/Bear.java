package animals;

import homes.Home;
import utils.Config;
import utils.HelperMethods;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.Set;

public class Bear extends Animal implements Actor {
    private static final int TERRITORY_RADIUS = 2;

    /***
     * Constructor that uses configs
     */
    public Bear() {
        super(Config.Bear.DIET, Config.Bear.DAMAGE, Config.Bear.HEALTH, Config.Bear.SPEED, Config.Bear.MATING_COOLDOWN_DAYS);
    }

    @Override
    public void beginAct(World w) {
        super.beginAct(w);
        if (home == null) {
            setHome(w, new Home(w.getLocation(this), 1, "Bear"));
        }
    }

    @Override
    public void awakeAct(World w) {
        super.awakeAct(w);
        if (isBedTime(w)) { goHome(w); }
        else {
            Object closestEdible = findTarget(w);
            if (closestEdible == null) {
                wander(w);
            } else {
                hunt(w, closestEdible);
            }
        }
    }

    /***
     * If satiation is greater than equal to 50 then it returns all tiles within territory else it returns tiles
     * within vision radius
     * @param w World
     * @return  Set of Locations
     */
    @Override
    public Set<Location> calcTilesInSight(World w) {
        if (satiation >= 50) {
            Set<Location> tilesInSight = w.getSurroundingTiles(getHomeLocation(), TERRITORY_RADIUS);
            tilesInSight.remove(w.getLocation(this));
            return tilesInSight;
        }
        return super.calcTilesInSight(w);
    }

    /***
     * If satiation is less than 50 then it super.randomMove else it to random location in the map
     * @param w World
     */
    @Override
    public void randomMove(World w) {
        if (satiation < 50) {
            super.randomMove(w);
            return;
        }

        Set<Location> validLocations = HelperMethods.getEmptySurroundingTiles(w, home.getLocation(), TERRITORY_RADIUS);
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