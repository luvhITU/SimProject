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

public class Grass extends Edible implements Actor, NonBlocking, DynamicDisplayInformationProvider {


    private static final int MAX_REPRODUCE_PER_DAY = 3;
    private int stepAge;
    private int age;

    private int reproductionsInLastDay;

    public Grass() {
        super(Config.Grass.NUTRITION);
        stepAge = 0;
        age = 0;
        reproductionsInLastDay = 0;
    }

    public void act(World w) {
        if (w.isDay()) { tryReproduce(w); }
        stepAge++;
        if (stepAge % World.getDayDuration() == 0) {
            age++;
            reproductionsInLastDay = 0;
            expirationCheck(w);
        }

    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, "grass");
    }

    public void expirationCheck(World w) {
        double expirationThreshold = 5.0;
        double expirationProbability = age / expirationThreshold;
        if (HelperMethods.getRandom().nextDouble() < expirationProbability) {
            delete(w);
        }
    }

    public void tryReproduce(World w) {
        if (reproductionsInLastDay == MAX_REPRODUCE_PER_DAY) { return; }
        double stopReproductionThreshold = 5.0;
        double reproduceProbability = 0.9 - age / stopReproductionThreshold;
        if (HelperMethods.getRandom().nextDouble() < reproduceProbability) {
            Set<Location> locations = w.getEmptySurroundingTiles();
            Location l = (Location) locations.toArray()[HelperMethods.getRandom().nextInt(locations.size())];

            if (!w.containsNonBlocking(l)) {
                //System.out.println("Placing grass at: " + l);
                w.setTile(l, new Grass());
                reproductionsInLastDay++;
            }
        }



    }
}
