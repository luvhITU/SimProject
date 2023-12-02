import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class Main {

    public static void main(String[] args) {
        String input = "data/t2-7a.txt";

        int size = HelperMethods.readWorldSize(input);
        int delay = 200;
        int display_size = 800;

        Program p = new Program(size, display_size, delay);
        World w = p.getWorld();
        /*
        HelperMethods.readObjects(input, w, p);
        */
        Wolf wolf = new Wolf();
        Wolf wolf2 = new Wolf();
        w.setTile(new Location(4,4),wolf);


        Rabbit rabbit = new Rabbit();
        w.setTile(new Location(12,12),rabbit);
        w.setTile(new Location(11,11), new Grass());
        p.show(); // viser selve simulationen
        for (int i = 0; i < 200; i++) {
            if(i == 5){
                for(int y = 0;y<4;y++){
                    w.setTile(new Location(y,y),new Wolf());
                }
            }
            if(i == 10){
                w.setTile(new Location(10,0), new Wolf());
            }
            p.simulate();
        }
    }
}