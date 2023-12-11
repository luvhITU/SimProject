package animals.packanimals;

import itumulator.world.World;
import utils.Config;

public class Fox extends PackAnimal {
    public Fox() {
        super(Config.Fox.DIET, Config.Fox.DAMAGE, Config.Fox.HEALTH, Config.Fox.SPEED, Config.Fox.MATING_COOLDOWN_DAYS, Config.Fox.MAX_PACK_SIZE, false);
    }

    @Override
    public void sleepAct(World w) {
        if (w.getCurrentTime() == 10 && canMate()) {
            burrowMatingPackage(w);
        }
        super.sleepAct(w);
    }
}
