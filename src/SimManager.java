import itumulator.executable.Program;
import itumulator.simulator.Simulator;
import itumulator.world.World;

import java.util.Random;

public enum SimManager {
    INSTANCE;
    private final Random random = new Random();
    private Program p;
    private Simulator s;
    private World w;
    private boolean isInitialized = false;

    public void initialize(int size, int display_size, int delay) {
        if (!isInitialized) {
            this.p = new Program(size, display_size, delay);
            this.s = p.getSimulator();
            this.w = p.getWorld();
            isInitialized = true;
        } else {
            throw new IllegalStateException("SimManager is already initialized.");
        }
    }

    public Random getRandom() {
        return random;
    }

    public Program getProgram() {
        checkInitialization();
        return p;
    }

    public Simulator getSimulator() {
        checkInitialization();
        return s;
    }

    public World getWorld() {
        checkInitialization();
        return w;
    }

    private void checkInitialization() {
        if (!isInitialized) {
            throw new IllegalStateException("SimManager is not initialized.");
        }
    }
}
