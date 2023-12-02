import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.Set;

public class Bear extends Animal implements Actor {

    private Location territoryCenter;
    private int territoryRadius = 2;

    public Bear(Location territoryCenter) {
        super(Config.Bear.DIET, Config.Bear.NUTRITION, Config.Bear.DAMAGE, Config.Bear.ABSORPTION_PERCENTAGE);
        this.territoryCenter = territoryCenter;
    }

    @Override
    public void act(World w) {
        if (getIsDead(w)) {
            return;
        }
        if (w.isNight()) {
            sleep();
        } else if (w.isDay() && !getIsAwake()) {
            wakeUp();
        }
        super.act(w);
        if (getIsAwake()) {
            doMovementPackage(w);
            if (!getHasMatedToday() && w.getCurrentTime() > 0) {
                tryToMate(w);
            }
            tryToEat(w);
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
        actionCost();
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

    @Override
    public void eat(World w, Edible edible) {
        if (edible instanceof Berry) {
            ((Berry) edible).eatBerries();
        }
        setEnergy(getEnergy() + calcNutritionAbsorbed(edible));
    }
}