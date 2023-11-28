import itumulator.executable.Program;
import itumulator.simulator.Simulator;
import itumulator.world.World;

import java.util.Random;

abstract class SimComponent {
    public Random getRandom() {
        return SimManager.INSTANCE.getRandom();
    }

    public Program getProgram() {
        return SimManager.INSTANCE.getProgram();
    }

    public Simulator getSimulator() {
        return SimManager.INSTANCE.getSimulator();
    }

    public World getWorld() {
        return SimManager.INSTANCE.getWorld();
    }

    public boolean getDeleted() {return !getWorld().getEntities().containsKey(this);}
}






