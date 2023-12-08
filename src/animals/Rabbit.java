package animals;

import utils.Config;
import itumulator.simulator.Actor;
import itumulator.world.World;

public class Rabbit extends Animal implements Actor {
    /***
     * Constructor that uses configs
     */
    public Rabbit() {
        super(Config.Rabbit.DIET, Config.Rabbit.DAMAGE, Config.Rabbit.HEALTH, Config.Rabbit.SPEED, Config.Rabbit.MATING_COOLDOWN_DAYS);
    }

    /***
     * See super
     * @param w providing details of the position on which the actor is currently located and much more.
     */
    @Override
    public void act(World w) {
        if (home == null) {
            tryFindOrDigBurrow(w, 5);
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
        if (isDead()) {
            delete(w);
        }
    }
}