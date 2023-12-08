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

    private int stepAge;

    public Grass() {
        super(Config.Grass.NUTRITION, 0);
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


    public boolean getIsDead(World w) {{
            expirationCheck(w);
            return w.isOnTile(this);
        }
    }

    public void expirationCheck(World w) {
        double divisor = 100.0;
        double expirationProbability = stepAge / divisor;
        if (HelperMethods.getRandom().nextDouble() < expirationProbability) {
            delete(w);
        }
    }

    public void tryReproduce(World w) {
        double divisor = 5.0;
        double expirationProbability = stepAge / divisor;
        if (HelperMethods.getRandom().nextDouble() < expirationProbability) {
            Set<Location> locations = w.getEmptySurroundingTiles(w.getLocation(this));
            Location l = (Location) locations.toArray()[HelperMethods.getRandom().nextInt(locations.size())];

            if (!w.containsNonBlocking(l)) {
                //System.out.println("Placing grass at: " + l);
                w.setTile(l, new Grass());
            }
        }
    }
}
