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
     * @param w providing details of the position on which the actor is currently located and much more.
     */
//    @Override
//    public void act(World w) {
//        super.act(w);
//        if (w.isDay()) {
//            reproduceCheck(w);
//        } else {
//            deleteCheck(w);
//        }
//
//
//    }

    /***
     * See super
     * @return  DisplayInformation
     */
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, "grass");
    }

    @Override
    public void age(World w) {
        super.age(w);
        reproduceCheck(w);
        deleteCheck(w);
    }

    /***
     * First checks if this needs to die and then returns a Boolean if it is dead or not. True means it is not dead
     * @param w World
     * @return  Boolean
     */



    /***
     * Uses a random chance and spreads if the random is meet and there is room in neighboring tiles
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

    private void deleteCheck(World w) {
        double p = 1.0 * stepAge / MAX_STEP_AGE;
        if (HelperMethods.getRandom().nextDouble() < p) { delete(w); }
    }
}
