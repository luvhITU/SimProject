package animals.packanimals;

import ediblesandflora.edibles.Edible;
import utils.Config;
import homes.Home;
import utils.HelperMethods;
import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.*;
import java.util.Set;

public class Wolf extends PackAnimal {
    private static final int MAX_PACK_SIZE = 5;

    /***
     * Constructor that uses configs
     */
    public Wolf() {
        super(Config.Wolf.DIET, Config.Wolf.DAMAGE, Config.Wolf.HEALTH, Config.Wolf.SPEED, Config.Wolf.MATING_COOLDOWN_DAYS, Config.Wolf.MAX_PACK_SIZE);
    }

    /***
     * See super
     * @param w providing details of the position on which the actor is currently located and much more.
     */
    @Override
    public void act(World w) {
        super.act(w);
        if (home == null && canBurrowHere(w)) {
            System.out.println(pack.getMembers().size());
            System.out.println("can burrow");
            burrow(w, maxPackSize);
        }
    }
}



