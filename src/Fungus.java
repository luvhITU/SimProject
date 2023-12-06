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

    public Fungus(int deadAnimalNutrition) {
        this.deadAnimalNutrition = deadAnimalNutrition;
        health = deadAnimalNutrition / 4;
        stepAge = 0;
    }

    @Override
    public void act(World w) {
        stepAge++;
        //System.out.println("Fungus Health: " + health);
        degrade(w);
        spread(w);
        //System.out.println(spreadRadius);
    }

    @Override
    public DisplayInformation getInformation() {
        String imageKey = deadAnimalNutrition > 100 ? "fungi" : "fungi-small";
        return new DisplayInformation(Color.orange, imageKey);
    }

    // Only runs every 2. step
    public void degrade(World w) {
        if (stepAge % 2 == 0) {
            health--;
            if (health <= 0) {
                delete(w);
            }
        }
    }

    public void spread(World w) {
        Set<Location> locations = w.getSurroundingTiles(w.getCurrentLocation(), spreadRadius);
        for (Location l : locations) {
            if (w.getTile(l) instanceof Carcass) {
                if (!((Carcass) w.getTile(l)).getIsInfected()) {
                    ((Carcass) w.getTile(l)).setInfected();
                    //System.out.println("Spread to: " + w.getTile(l));
                }
            }
        }
        // Only runs every 2. step
        if (stepAge % 2 == 0 && spreadRadius < spreadRadiusMax) {
            spreadRadius++;
        }
    }

    public void delete(World w) {
        w.delete(this);
    }
}
