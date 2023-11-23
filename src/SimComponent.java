import itumulator.executable.Program;
import itumulator.simulator.Simulator;
import itumulator.world.World;

public class SimComponent {
    public Program p;
    public Simulator s;
    public World w;

    public SimComponent(Program p) {
        this.p = p;
        s = p.getSimulator();
        w = p.getWorld();
    }

    public Object getCurrentNonBlocking() {
        return w.getNonBlocking(w.getCurrentLocation());
    }
}




