import itumulator.executable.Program;
import itumulator.world.World;

public class Main {

    public static void main(String[] args) {
        String input = "data/rabbits.txt";

        int size = HelperMethods.readWorldSize(input);
        int delay = 100;
        int display_size = 800;

        SimManager.INSTANCE.initialize(size, display_size, delay);
        Program p = SimManager.INSTANCE.getProgram();
        World world = p.getWorld();
        HelperMethods.readObjects(input, world, p);

        p.show(); // viser selve simulationen
        for (int i = 0; i < 200; i++) {
            p.simulate();
        }
    }
}