import itumulator.executable.Program;
import itumulator.world.World;

public class Main {

    public static void main(String[] args) {
        String input = "data/t1-1d.txt";

        int size = HelperMethods.readWorldSize(input);
        int delay = 100;
        int display_size = 800;

        Program p = new Program(size, display_size, delay);
        World world = p.getWorld();
        HelperMethods.readObjects(input, world, p);

        p.show(); // viser selve simulationen
        for (int i = 0; i < 200; i++) {
            p.simulate();
        }
    }
}