import utils.HelperMethods;
import itumulator.executable.Program;
import itumulator.world.World;

public class Main {

    public static void main(String[] args) {
        String input = "data/tf2-1.txt";

        int size = HelperMethods.readWorldSize(input);
        int delay = 1000;
        int display_size = 800;

        Program p = new Program(size, display_size, delay);
        World w = p.getWorld();
        HelperMethods.readObjects(input, w, p);

        p.show(); // viser selve simulationen
        for (int i = 0; i < 200; i++) {
            p.simulate();
        }
    }
}
