import itumulator.executable.Program;
import itumulator.simulator.Simulator;
import itumulator.world.World;

import java.util.Random;

public abstract class SimComponent {
    public boolean getIsDead(World w) {
        return !w.getEntities().containsKey(this);
    }
}








