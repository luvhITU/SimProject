package animals;

import animals.packanimals.Wolf;
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

    @Override
    public void beginAct(World w) {
        super.beginAct(w);
        if (home == null) {
            tryFindOrDigBurrow(w, 5);
        }
    }

    @Override
    public void sleepAct(World w) {
        if (w.getCurrentTime() == 0 && canMate()) {
            burrowMatingPackage(w);
        }
        super.sleepAct(w);
    }

    @Override
    public void awakeAct(World w) {
        super.awakeAct(w);
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

    @Override
    public Animal findClosestPartner(World w) {
        for (Animal rabbit : home.getOccupants()) {
            if (!rabbit.isAwake && rabbit.canMate()) {
                return rabbit;
            }
        }
        return null;
    }
}