import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;
import java.awt.Color;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        int size = 15;
        int delay = 1000;
        int display_size = 800;
        Program p = new Program(size, display_size, delay);
        World w = p.getWorld();

        Random r = new Random();

        for (int i = 0; i < 10; i++) {
            int x = r.nextInt(size);
            int y = r.nextInt(size);
            Location l = new Location(x, y);
            while (!w.isTileEmpty(l)) {
                x = r.nextInt(size);
                y = r.nextInt(size);
                l = new Location(x, y);
            }
            w.setTile(l, new Rabbit(p));
        }

        Grass grass = new Grass(p);
        Location gPlace = new Location(1, 2);
        w.setTile(gPlace, grass);

        //System.out.println(world.getEmptySurroundingTiles(gPlace));

        DisplayInformation di = new DisplayInformation(Color.red);
        p.setDisplayInformation(Rabbit.class, di);

        p.show(); // viser selve simulationen
        for (int i = 0; i < 200; i++) {
            p.simulate();
        }
    }
}