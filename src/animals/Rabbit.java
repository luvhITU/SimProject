package animals;

import animals.packanimals.Wolf;
import itumulator.world.Location;
import utils.Config;
import itumulator.simulator.Actor;
import itumulator.world.World;

import java.util.Set;

public class Rabbit extends Animal implements Actor {
    /***
     * Constructor that uses configs
     */
    public Rabbit() {
        super(Config.Rabbit.DIET, Config.Rabbit.DAMAGE, Config.Rabbit.HEALTH, Config.Rabbit.SPEED, Config.Rabbit.MATING_COOLDOWN_DAYS);
    }

    /***
     * See super
     * @param w World
     */
    @Override
    public void sleepAct(World w) {
        if (w.getCurrentTime() == 0 && canMate()) {
            burrowMatingPackage(w);
        }
        super.sleepAct(w);
    }

    /***
     * See super
     * @param w World
     */
    @Override
    public void awakeAct(World w) {
        super.awakeAct(w);
        Animal predator = findClosestPredator(w);
        if (predator != null) {
            flee(w, w.getLocation(predator));
        } else if (isBedTime(w)) {
            if (home == null) {
                tryFindOrDigBurrow(w, 5);
            } else {
                goHome(w);
            }
        } else {
            Object target = findTarget(w);
            if (target == null) {
                randomMove(w);
            } else {
                hunt(w, target);
            }
        }
    }
}