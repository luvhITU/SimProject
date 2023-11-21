import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.world.NonBlocking;
import java.util.Set;
import java.util.Random;
import java.awt.Color;

public class Grass implements Actor, NonBlocking, DynamicDisplayInformationProvider, Perishable, Eatable, Reproduction {
    int age = 0;

    public void act(World world) {
        //Spread grass
        reproduce(world);

        //Test if needs to die
        deadFromAge(world);
        age++;
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.green, "grass");
    }

    @Override
    public void deadFromAge(World world) {
        int chance = new Random().nextInt(10+age);
        if (chance > 10) {
            world.delete(this);
        }
    }
    @Override
    public void reproduce(World world){
        Set<Location> locations = world.getEmptySurroundingTiles();
        Location l = (Location) locations.toArray()[new Random().nextInt(locations.size())];

        if (!world.containsNonBlocking(l)) {
            //System.out.println("Placing grass at: " + l);
            world.setTile(l, new Grass());
        }
    }
}
