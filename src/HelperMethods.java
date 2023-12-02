import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.Program;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public abstract class HelperMethods {
    private static final Random r = new Random();
    private static List<Location> occupied = new ArrayList<>();

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
            occupied.clear();
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
        int rValue = r.nextInt((endRange + 1) - startRange) + startRange;
        if (startRange != endRange) { System.out.println("Range Value: " + rValue); }

        for (int i = 0; i < rValue; i++) {
            Location l = getRandomEmptyLocation(w, r);
            occupied.add(l);
            
            if (type.equals("grass")) {
                w.setTile(l, new Grass());
            } else if (type.equals("rabbit")) {
                w.setTile(l, new Rabbit());
            } else if (type.equals("burrow")) {
                w.setTile(l, new RabbitBurrow());
            } else if (type.equals("berry")) {
                w.setTile(l, new Berry());
                //System.out.println(l);
            } else if (type.equals("bear")) {
                if (!(x == -1 && y == -1)) {
                    w.setTile(l, new Bear(new Location(x, y)));
                } else {
                    w.setTile(l, new Bear(l));
                }
            }
        }

        List<Home> rabbitBurrows = HelperMethods.availableHomes(w, "RabbitBurrow");
        Set<Object> entitiesKeys = w.getEntities().keySet();
        for (Home h : rabbitBurrows) {
            for (Object e : entitiesKeys) {
                if (!h.isAvailable()) { break; }
                if (e instanceof Rabbit) {
                    ((Rabbit) e).setHome(w, h);
                }
                ;
            }
        }

        // ONLY USED TO VISUALIZE BEAR TERRITORY
        if (!(x == -1 && y == -1)) {
            w.setTile(new Location(x, y), new BearTerritory());
        }
        occupied.clear();
    }

    private static Location getRandomEmptyLocation(World w, Random r) {
        int x, y;
        Location l;

        do {
            x = r.nextInt(w.getSize());
            y = r.nextInt(w.getSize());
            l = new Location(x, y);
        } while (occupied.contains(l));
        return l;
    }

    public static ArrayList<Home> availableHomes(World w, String type) {
        Map<Object, Location> entities = w.getEntities();
        ArrayList<Home> availableHomes = new ArrayList<>();
        for (Object e : entities.keySet()) {
            if (!(e instanceof Home) ) {
                continue;
            }
            Home home = (Home) e;
            if (home.isAvailable() && home.getClass().getSimpleName().equals(type)) {
                availableHomes.add(home);
            }
        }
        return availableHomes;
    }

    public static Location getClosestEmptyTile(World w, Location loc, int radius) {
        Set<Location> oldTargetTiles = new HashSet<>();
        for (int r = 1; r <= radius; r++) {
            Set<Location> targetTiles = w.getSurroundingTiles(loc, r);
            targetTiles.remove(oldTargetTiles);
            for (Location l : targetTiles) {
                if (w.isTileEmpty(l)) {
                    return l;
                }
            }
            oldTargetTiles = new HashSet<>(targetTiles);
        } throw new IllegalStateException("No empty tiles within set radius");
    }
}


