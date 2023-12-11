package animals.packanimals;

import utils.Config;
import itumulator.world.World;

public class Wolf extends PackAnimal {
    private static final int MAX_PACK_SIZE = 5;

    /***
     * Constructor that uses configs
     */
    public Wolf() {
        super(Config.Wolf.DIET, Config.Wolf.DAMAGE, Config.Wolf.HEALTH, Config.Wolf.SPEED, Config.Wolf.MATING_COOLDOWN_DAYS, Config.Wolf.MAX_PACK_SIZE, true);
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
        if (w.isNight() && home == null && canBurrowHere(w)) {
            burrow(w, maxPackSize);
        }
        super.awakeAct(w);
    }
}



