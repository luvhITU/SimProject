import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.Program;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public abstract class HelperMethods {
    private static final Random r = new Random();

    public static Random getRandom() {return r;}
    public static int readWorldSize(String input) {
        String filePath = input;
        int worldSize = 0;

        try {
            Scanner scanner = new Scanner(new File(filePath));

            if (scanner.hasNextInt()) {
                worldSize = scanner.nextInt();
                System.out.println("World Size: " + worldSize);
                scanner.nextLine();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return worldSize;
    }

    public static void readObjects(String input, World w, Program p) {
        String filePath = input;
        int amount = 0, startRange = 0, endRange = 0, x = 0, y = 0;
        String type = null;

        try {
            Scanner sc = new Scanner(new File(filePath));
            sc.nextLine(); // Skip first line

            while (sc.hasNextLine()) {
                String str = sc.nextLine().trim();

                // Skip empty lines
                if (str.isEmpty()) {
                    continue;
                }

                String[] tokens = str.split("[\\s-,()]+");

                type = tokens[0];
                System.out.println("Type: " + type);

                if (tokens.length == 2) {
                    amount = Integer.parseInt(tokens[1]);
                    System.out.println("Amount: " + amount);
                    spawnObject(w, p, type, amount, -1, -1);
                } else if (tokens.length == 3) {
                    startRange = Integer.parseInt(tokens[1]);
                    endRange = Integer.parseInt(tokens[2]);
                    System.out.println("Range: [" + startRange + ", " + endRange + "]");
                    spawnObject(w, p, type, startRange, endRange, -1, -1);
                } else if (tokens.length == 4) {
                    amount = Integer.parseInt(tokens[1]);
                    x = Integer.parseInt(tokens[2]);
                    y = Integer.parseInt(tokens[3]);
                    System.out.println("Amount: " + amount);
                    System.out.println("Territory Center: (" + x + "," + y + ")");
                    spawnObject(w, p, type, amount, x, y);
                }

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void spawnObject(World w, Program p, String type, int amount, int x, int y) {
        spawnObjects(w, p, type, amount, amount, x, y);
    }

    public static void spawnObject(World w, Program p, String type, int startRange, int endRange, int x, int y) {
        spawnObjects(w, p, type, startRange, endRange, x, y);
    }

    private static void spawnObjects(World w, Program p, String type, int startRange, int endRange, int x, int y) {
        List<Location> occupied = new ArrayList<>();
        int rValue = r.nextInt((endRange + 1) - startRange) + startRange;
        if (startRange != endRange) { System.out.println("Range Value: " + rValue); }

        for (int i = 0; i < rValue; i++) {
            Location l = getRandomEmptyLocation(w, r, occupied);
            occupied.add(l);

            if (type.equals("grass")) {
                //System.out.println(l);
                w.setTile(l, new Grass());
            } else if (type.equals("rabbit")) {
                w.setTile(l, new Rabbit());
            } else if (type.equals("burrow")) {
                w.setTile(l, new Hole());
            } else if (type.equals("wolf")) {
                //TODO: Spawn Wolf Object
            } else if (type.equals("bear")) {
                //TODO: Spawn Bear Object
            }
        }

        if (!(x == -1 && y == -1)) {
            w.setTile(new Location(x, y), null); //TODO: Spawn Bear Territory
        }
        occupied.clear();
    }

    private static Location getRandomEmptyLocation(World w, Random r, List<Location> occupied) {
        int x, y;
        Location l;

        do {
            x = r.nextInt(w.getSize());
            y = r.nextInt(w.getSize());
            l = new Location(x, y);
        } while (occupied.contains(l));
        return l;
    }

    public boolean getIsDeleted(World w, Object obj) {
        return w.getEntities().containsKey(obj);
    }
}
