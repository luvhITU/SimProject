package MapComponents;

import Abstracts.Animal;
import Helper.Config;
import Places.RabbitBurrow;
import itumulator.simulator.Actor;
import itumulator.world.World;

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
                Object target = findClosestEdible(w);
                if (target == null) {
                    randomMove(w);
                } else {
                    hunt(w, target);
                }

            }
        }
        if (isDead()) { delete(w); }
    }

    private void findOrDigBurrow(World w) {
        tryFindHome(w, "Places.RabbitBurrow");
        if (home == null) {
            digBurrow(w, new RabbitBurrow());
        }
    }
}