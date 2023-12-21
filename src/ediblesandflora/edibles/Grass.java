package ediblesandflora.edibles;

import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.world.NonBlocking;
import utils.Config;
import utils.HelperMethods;

import java.util.Set;
import java.awt.Color;

public class Grass extends Edible implements NonBlocking, DynamicDisplayInformationProvider {

    private static final int MAX_STEP_AGE = 100;

    /***
     * Constructor for Grass
     */
    public Grass() {
        super(Config.Grass.NUTRITION, 0);
    }

    /***
     * See super
     * @return  DisplayInformation
     */
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, "grass");
    }

    /***
     * See super
     * @param w World
     */
    @Override
    public void age(World w) {
        super.age(w);
        reproduceCheck(w);
        deleteCheck(w);
    }

    /***
     * Runs a probability check based on age, and spreads the grass if check succeeds
     * @param w World
     */
    public void reproduceCheck(World w) {
        Set<Location> neighbours = w.getEmptySurroundingTiles(w.getLocation(this));
        neighbours.removeIf(w::containsNonBlocking);
        if (neighbours.isEmpty()) { return; }

        int offset = 20;
        double p = 1 - 1.0 * stepAge / MAX_STEP_AGE + offset;
        if (HelperMethods.getRandom().nextDouble() < p) {
            Location locToPlaceGrass = (Location) neighbours.toArray()[HelperMethods.getRandom().nextInt(neighbours.size())];
            w.setTile(locToPlaceGrass, new Grass());
        }
    }

    /***
     * Runs a probability check based on age, and deletes the grass if check succeeds
     * @param w World
     */
    private void deleteCheck(World w) {
        double p = 1.0 * stepAge / MAX_STEP_AGE;
        if (HelperMethods.getRandom().nextDouble() < p) { delete(w); }
    }
}
