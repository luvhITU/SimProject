import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Set;

public class Rabbit extends Animal implements Actor {
    public Rabbit() {
        super(Config.Rabbit.DIET, Config.Rabbit.DAMAGE, Config.Rabbit.HEALTH, Config.Rabbit.SPEED, Config.Rabbit.MATING_COOLDOWN_DAYS);
    }

    @Override
    public void act(World w) {
        if (home == null) {
            findOrDigBurrow(w);
        }
        super.act(w);

        if (isAwake) {
            Animal predator = findClosestPredator(w);
            if (predator != null) {
                flee(w, w.getLocation(predator));
            } else if (w.isNight()) {
                goHome(w);
            } else {
                hunt(w);
            }
        }
    }

    private void findOrDigBurrow(World w) {
        tryFindHome(w, "RabbitBurrow");
        if (home == null) {
            digBurrow(w, new RabbitBurrow());
        }
    }
}