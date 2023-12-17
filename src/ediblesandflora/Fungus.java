package ediblesandflora;

import ediblesandflora.edibles.Carcass;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.*;
import java.util.Set;

public class Fungus implements Actor, DynamicDisplayInformationProvider {

    private final int deadAnimalNutrition;
    private int health;
    private int stepAge;
    private int spreadRadius = 0;
    private int spreadRadiusMax = 5;

    /***
     * Initializes "deadAnimalNutrition" and initializes health to be 1/4 of "deadAnimalNutrition"
     * @param deadAnimalNutrition   int
     */
    public Fungus(int deadAnimalNutrition) {
        this.deadAnimalNutrition = deadAnimalNutrition;
        health = deadAnimalNutrition / 4;
        stepAge = 0;
    }

    /***
     * See super
     * @param w providing details of the position on which the actor is currently located and much more.
     */
    @Override
    public void act(World w) {
        stepAge++;
        //System.out.println("ediblesandflora.Fungus Health: " + health);
        degrade(w);
        spread(w);
        //System.out.println(spreadRadius);
    }

    /***
     * See super
     * @return  DisplayInformation
     */
    @Override
    public DisplayInformation getInformation() {
        String imageKey = deadAnimalNutrition > 100 ? "fungi" : "fungi-small";
        return new DisplayInformation(Color.orange, imageKey);
    }

    // Only runs every 2. step

    /***
     * Does "health--;" every 2nd world tick
     * @param w World
     */
    public void degrade(World w) {
        if (stepAge % 2 == 0) {
            health--;
            if (health <= 0) {
                delete(w);
            }
        }
    }

    /***
     * Sees if there is any uninfected carcass in "spreadRadius" and spreads to the carcass every 2nd world tick
     * @param w World
     */
    public void spread(World w) {
        try {
            Set<Location> locations = w.getSurroundingTiles(w.getCurrentLocation(), spreadRadius);
            for (Location l : locations) {
                if (w.getTile(l) instanceof Carcass) {
                    if (!((Carcass) w.getTile(l)).getIsInfected()) {
                        ((Carcass) w.getTile(l)).setInfected();
                        //System.out.println("Spread to: " + w.getTile(l));
                    }
                }
            }
        }
        catch (NullPointerException ignore){ } //Catches if the location is null
        // Only runs every 2. step
        if (stepAge % 2 == 0 && spreadRadius < spreadRadiusMax) {
            spreadRadius++;
        }
    }

    /***
     * Deletes this from the world
     * @param w World
     */
    public void delete(World w) {
        w.delete(this);
    }
}
