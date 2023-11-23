import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.world.NonBlocking;
import java.util.Set;
import java.util.Random;
import java.awt.Color;

public class Grass extends SimComponent implements Actor, NonBlocking, DynamicDisplayInformationProvider, Perishable, Reproduction, Edible {
    private int stepAge;
    private int nutrition;

    public Grass(Program p) {
        super(p);
        stepAge = 0;
        nutrition = 1;
    }

    public void act(World world) {
        //Spread grass
        reproduce();

        //Test if needs to die
        expirationCheck();
        stepAge++;
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, "grass");
    }

    public void expirationCheck() {
        int initialUpperBound = 30;
        double chance = new Random().nextInt(initialUpperBound + 1 + stepAge);
        if (chance > initialUpperBound) {
            w.delete(this);
        }
    }

    public void reproduce() {
        Set<Location> locations = w.getEmptySurroundingTiles();
        Location l = (Location) locations.toArray()[new Random().nextInt(locations.size())];

        if (!w.containsNonBlocking(l)) {
            //System.out.println("Placing grass at: " + l);
            w.setTile(l, new Grass(p));
        }
    }

    public int getNutrition() {
        return nutrition;
    }
}
