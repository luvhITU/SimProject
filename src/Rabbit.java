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
        try {
            super.act(w);
            if (w.isDay()) {move();}
        } catch(IllegalArgumentException ignored) {}
        tryToEat();
    }

    @Override
    public DisplayInformation getInformation() {
        if (w.isNight()) {
            return new DisplayInformation(Color.blue, "rabbit-sleeping");
        } else {
            return new DisplayInformation(Color.gray, "rabbit-large");
        }
    }

    public void move(){
        Set<Location> neighbours = w.getEmptySurroundingTiles();
        Location l = (Location) neighbours.toArray()[new Random().nextInt(neighbours.size())];
        w.move(this, l);
    }

    void tryToEat() {
        try {
            Object nonBlocking = getCurrentNonBlocking();
            if (getFoodSources().contains(nonBlocking.getClass().getSimpleName())) {
                Edible EdibleObject = (Edible) nonBlocking;
                setSatiation(Math.max(EdibleObject.getNutrition(), getMaxSatiation()));
                EdibleObject.kill();
            }
        } catch(IllegalArgumentException e) {return;}
    }
}

