import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.Location;
import java.util.Random;

public class Person implements Actor, DynamicDisplayInformationProvider {
    boolean isNight = false;

    @Override
    public void act(World world) {
        isNight = world.isNight();
        if (world.isDay()) {
            Set<Location> neighbours = world.getEmptySurroundingTiles();
            List<Location> list = new ArrayList<>(neighbours);
            Location l = list.get(new Random().nextInt(list.size()));
            world.move(this, l);
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if (isNight) {
            return new DisplayInformation(Color.blue, "bear-sleeping");
        } else {
            return new DisplayInformation(Color.gray, "bear");
        }
    }
}