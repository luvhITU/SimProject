import itumulator.world.Location;
import itumulator.world.World;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class HelperMethods {

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

    public static void readObjects(String input, World world) {
        String filePath = input;
        int amount = 0, startRange = 0, endRange = 0;
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

                String[] tokens = str.split("[\\s-]+");

                type = tokens[0];
                System.out.println("Type: " + type);

                if (tokens.length == 2) {
                    amount = Integer.parseInt(tokens[1]);
                    System.out.println("Amount: " + amount);
                    HelperMethods.spawnObject(world, type, amount);
                } else if (tokens.length == 3) {
                    startRange = Integer.parseInt(tokens[1]);
                    endRange = Integer.parseInt(tokens[2]);
                    System.out.println("Range: [" + startRange + ", " + endRange + "]");
                    HelperMethods.spawnObject(world, type, startRange, endRange);
                }

            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void spawnObject(World world, String type, int amount) {
        spawnObjects(world, type, amount, amount);
    }

    public static void spawnObject(World world, String type, int startRange, int endRange) {
        spawnObjects(world, type, startRange, endRange);
    }

    private static void spawnObjects(World world, String type, int startRange, int endRange) {
        Random r = new Random();
        int rValue = r.nextInt((endRange + 1) - startRange) + startRange;

        for (int i = 0; i < rValue; i++) {
            Location l = getRandomEmptyLocation(world, r);
            if (type.equals("grass")) {
                world.setTile(l, new Grass());
            } else if (type.equals("rabbit")) {
                // TODO: Spawn Rabbit
            } else if (type.equals("burrow")) {
                // TODO: Spawn Rabbithole
            }
        }
    }

    private static Location getRandomEmptyLocation(World world, Random r) {
        int x, y;
        Location l;

        do {
            x = r.nextInt(world.getSize());
            y = r.nextInt(world.getSize());
            l = new Location(x, y);
        } while (world.containsNonBlocking(l));
        return l;
    }
}
