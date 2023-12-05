import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.Set;

public class Bear extends Animal implements Actor {

    private Location territoryCenter;
    private int territoryRadius = 2;

    public Bear(Location territoryCenter) {
        super(Config.Bear.DIET, Config.Bear.NUTRITION, Config.Bear.DAMAGE, Config.Bear.HEALTH, Config.Bear.SPEED);
        this.territoryCenter = territoryCenter;
    }

    @Override
    public void act(World w) {
        if (w.isNight()) {
            sleep(w);
        } else if (w.isDay() && !getIsAwake()) {
            wakeUp(w);
        }
        super.act(w);
        if (getIsAwake()) {
            doMovementPackage(w);
            hunt(w);
        }
    }

    private void doMovementPackage(World w) {
        if (w.isDay()) {
            moveAroundTerritory(w);
        }
    }

    public void moveAroundTerritory(World w) {
        Set<Location> validLocations = w.getSurroundingTiles(territoryCenter, territoryRadius);
        validLocations.removeIf(location -> !w.isTileEmpty(location));

        if (validLocations.isEmpty()) {
            return;
        }

        Location newLocation = validLocations.toArray(new Location[0])[HelperMethods.getRandom().nextInt(validLocations.size())];
        w.move(this, newLocation);
        actionCost(2);
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