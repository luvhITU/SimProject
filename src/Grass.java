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

    private int stepAge;

    public Grass() {
        super(Config.Grass.NUTRITION);
        stepAge = 0;
    }

    public void act(World w) {
        if (getIsDead(w)) {return;}
        stepAge++;
        if (w.isDay()) {
            tryReproduce(w);
        }
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, "grass");
    }

    @Override
    public boolean getIsDead(World w) {
        if (!super.getIsDead(w)) {
            expirationCheck(w);
        }
        return super.getIsDead(w);
    }

    public void expirationCheck(World w) {
        int max_step_age = 100;
        double expirationProbability = 1.0 * stepAge / max_step_age;
        if (HelperMethods.getRandom().nextDouble() < expirationProbability) {
            w.delete(this);
        }
    }

    public void tryReproduce(World w) {
        Set<Location> locations = w.getEmptySurroundingTiles();
        Location l = (Location) locations.toArray()[HelperMethods.getRandom().nextInt(locations.size())];

        if (!w.containsNonBlocking(l)) {
            //System.out.println("Placing grass at: " + l);
            w.setTile(l, new Grass());
        }
    }
}
