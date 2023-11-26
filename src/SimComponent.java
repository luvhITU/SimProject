import itumulator.executable.Program;
import itumulator.simulator.Simulator;
import itumulator.world.Location;
import itumulator.world.World;

abstract class SimComponent {
    public Program p;
    public Simulator s;
    public World w;

    public SimComponent(Program p) {
        this.p = p;
        s = p.getSimulator();
        w = p.getWorld();
    }

    public void die() {
        try {
            w.delete(this);
        } catch (IllegalArgumentException e) {
            System.out.println(this + " was deleted by another process before it could be deleted by .die() method");
        }
    }
}





