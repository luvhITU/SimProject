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

    public void act(World world) {
        if(getDeleted()) {return;}
        stepAge++;
        if (getWorld().isDay()) {
            reproduce();
        }
        expirationCheck();

    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, "grass");
    }

    public void expirationCheck() {
        int max_step_age = 100;
        double expirationProbability = 1.0 * stepAge / max_step_age;
        if (getRandom().nextDouble() < expirationProbability) {
            getWorld().delete(this);
        }
    }

    public void reproduce() {
        Set<Location> locations = getWorld().getEmptySurroundingTiles();
        Location l = (Location) locations.toArray()[new Random().nextInt(locations.size())];

        if (!getWorld().containsNonBlocking(l)) {
            //System.out.println("Placing grass at: " + l);
            getWorld().setTile(l, new Grass());
        }
    }
}
