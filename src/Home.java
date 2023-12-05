import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.*;

public class Home extends SimComponent implements Actor {
    private int maxAllowed;
    private Set<AnimalOLD> occupants;

    public Home(int maxAllowed) {
        this.maxAllowed = maxAllowed;
        occupants = new HashSet<AnimalOLD>();
    }

    public void act(World w) {
        if (getIsDead(w)) { return; }
        updateOccupants(w);
    }

    public void add(AnimalOLD animal) {
        if (!isAvailable()) { throw new IllegalStateException("Home is filled to brim already!"); }
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
