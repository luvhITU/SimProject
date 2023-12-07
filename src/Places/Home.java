package Places;

import Abstracts.Animal;
import Abstracts.SimComponent;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public class Home extends SimComponent implements Actor {
    private int maxAllowed;
    private Set<Animal> occupants;

    public Home(int maxAllowed) {
        this.maxAllowed = maxAllowed;
        occupants = new HashSet<Animal>();
    }

    public void act(World w) {
        updateOccupants(w);
    }

    public void add(Animal animal) {
        if (!isAvailable()) { throw new IllegalStateException("Places.Home is filled to brim already!"); }
        occupants.add(animal);
    }

    public boolean isAvailable() {
        return occupants.size() < maxAllowed;
    }

    private void updateOccupants(World w) {
        Map<Object, Location> entities = w.getEntities();
        occupants.removeIf(next -> !entities.containsKey(next));
    }
}
