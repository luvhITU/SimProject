import java.awt.Color;
import java.util.*;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.Location;

public class Rabbit extends Animal implements Actor, DynamicDisplayInformationProvider {

    public Rabbit(Program p) {
        super(p, new HashSet<String>(Set.of("Grass")), 1.1);
    }

    @Override
    public void act(World w) {
        super.act(w);
        if (isAwake) {
            Set<Location> neighbours = w.getEmptySurroundingTiles();
            if (!neighbours.isEmpty()) {randomMove(neighbours);}
            tryToEat();
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if (w.isNight()) {
            return new DisplayInformation(Color.blue, "rabbit-sleeping");
        } else {
            return new DisplayInformation(Color.gray, "rabbit-large");
        }
    }

    public void randomMove(Set<Location> neighbours){
        try {
            Location l = (Location) neighbours.toArray()[new Random().nextInt(neighbours.size())];
            w.move(this, l);
        } catch(IllegalArgumentException e) {
            System.out.println(this + " was deleted by another process before it be moved");
        }
    }

    public void tryToEat() {
        Location l = w.getCurrentLocation();
        if (!w.containsNonBlocking(l)) {
            return;
        }
        Object nonBlocking = w.getNonBlocking(l);
        if (getFoodSources().contains(nonBlocking.getClass().getSimpleName())) {
            Edible edibleObject = (Edible) nonBlocking;
            eat(edibleObject);
        }
    }
}

