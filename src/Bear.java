import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.Location;

import java.util.HashSet;
import java.util.Set;

public class Bear extends Animal implements Actor {
    private static final int TERRITORY_RADIUS = 4;
    private Set<Location> territory;
    private Location homeLocation;

    public Bear() {
        super(Config.Bear.DIET, Config.Bear.DAMAGE, Config.Bear.HEALTH, Config.Bear.AGGRESSION, Config.Bear.SPEED);
    }

    public void act(World w) {
        if (getIsDead()) { return; }
        if (getHome() == null) {
            homeLocation = w.getCurrentLocation();
            setHome(w, new BearHome());
            territory = w.getSurroundingTiles(TERRITORY_RADIUS);
        } else if (territory.contains(w.getCurrentLocation())) {
            Set<Location> tilesInSight = new HashSet<>(territory);
            tilesInSight.remove(w.getCurrentLocation());
            setTilesInSight(tilesInSight);
        } else {
            setTilesInSight(w.getSurroundingTiles(5));
        }
        if (!getIsAwake() && w.getCurrentTime() == 0) {
            wakeUp(w);
        }
        super.act(w);
    }

    @Override
    public Location getLocation(World w, Object object) {
        if (object == getHome()) {
            return homeLocation;
        }
        return super.getLocation(w, object);
    }
}
