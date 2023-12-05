import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.Program;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public abstract class HelperMethods {
    private static final Random r = new Random();
    private static List<Location> occupied = new ArrayList<>();

    public static Random getRandom() {
        return r;
    }

    /**
     * Parses .txt-file and returns WorldSize as Integer.
     * @param input FilePath as String to be parsed.
     * @return WorldSize from .txt-file.
     */
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

    /**
     * Parses .txt-file and reads Objects to be spawned.
     * @param input FilePath as String to be parsed.
     * @param w World
     * @param p Program
     */
    public static void readObjects(String input, World w, Program p) {
        String filePath = input;
        int amount = 0, startRange = 0, endRange = 0, x = 0, y = 0;
        String type = null;
        boolean isInfected = false;

        try {
            Scanner sc = new Scanner(new File(filePath));
            sc.nextLine(); // Skip first line

            while (sc.hasNextLine()) {
                String str = sc.nextLine().trim().toLowerCase();

                // Skip empty lines
                if (str.isEmpty()) {
                    continue;
                }

                // if String contains "cordyceps" then the Animal-Object should be infected
                if (str.contains("cordyceps")) {
                    isInfected = true;
                }

                // Checks if input files contains "cordyceps", then splits into tokens-Array
                String[] tokens = str.replaceFirst("cordyceps ", "").split("[\\s-,()]+");

                type = tokens[0];
                System.out.println("Type: " + type);
                System.out.println("Is Infected: " + isInfected);

                if (tokens.length == 2) {
                    amount = Integer.parseInt(tokens[1]);
                    System.out.println("Amount: " + amount);
                    spawnObject(w, p, isInfected, type, amount, -1, -1);
                } else if (tokens.length == 3) {
                    startRange = Integer.parseInt(tokens[1]);
                    endRange = Integer.parseInt(tokens[2]);
                    System.out.println("Range: [" + startRange + "-" + endRange + "]");
                    spawnObject(w, p, isInfected, type, startRange, endRange, -1, -1);
                } else if (tokens.length == 4) {
                    amount = Integer.parseInt(tokens[1]);
                    x = Integer.parseInt(tokens[2]);
                    y = Integer.parseInt(tokens[3]);
                    System.out.println("Amount: " + amount);
                    System.out.println("Territory Center: (" + x + "," + y + ")");
                    spawnObject(w, p, isInfected, type, amount, x, y);
                }
                isInfected = false;
                System.out.println(); // Empty line
            }
            occupied.clear();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Spawns a certain amount of Object(s).
     * @param w World
     * @param p Program
     * @param isInfected Boolean. Returns True if Animal is infected, False if not.
     * @param type Type of Object to be spawned.
     * @param amount Amount of Object(s) to be spawned.
     * @param x x-Coordinate of Bear-Territory.
     * @param y y-Coordinate of Bear-Territory.
     */
    public static void spawnObject(World w, Program p, boolean isInfected, String type, int amount, int x, int y) {
        spawnObjects(w, p, isInfected, type, amount, amount, x, y);
    }

    /**
     * Spawns a certain amount of Object(s) between a Range.
     * @param w World
     * @param p Program
     * @param isInfected Boolean. Returns True if Animal is infected, False if not.
     * @param type Type of Object to be spawned.
     * @param startRange Minimum amount of Objects to be spawned.
     * @param endRange  Maximum amount of Objects to be spawned.
     * @param x x-Coordinate of Bear-Territory.
     * @param y y-Coordinate of Bear-Territory.
     */
    public static void spawnObject(World w, Program p, boolean isInfected, String type, int startRange, int endRange, int x, int y) {
        spawnObjects(w, p, isInfected, type, startRange, endRange, x, y);
    }

    /**
     * Spawns Object(s) in the World.
     * @param w World
     * @param p Program
     * @param isInfected Boolean. Returns True if Animal is infected, False if not.
     * @param type Type of Object to be spawned.
     * @param startRange Minimum amount of Objects to be spawned.
     * @param endRange  Maximum amount of Objects to be spawned.
     * @param x x-Coordinate of Bear-Territory.
     * @param y y-Coordinate of Bear-Territory.
     */
    private static void spawnObjects(World w, Program p, boolean isInfected, String type, int startRange, int endRange, int x, int y) {
        int rValue = r.nextInt((endRange + 1) - startRange) + startRange;
        if (startRange != endRange) {
            System.out.println("Range Value: " + rValue);
        }

        for (int i = 0; i < rValue; i++) {
            Location l = getRandomEmptyLocation(w, r);
            occupied.add(l);

            //TODO: Add logic to infect spawned Animal-Objects. Maybe as parameter in Animal constructor?
            if (type.equals("grass")) {
                w.setTile(l, new Grass());
            } else if (type.equals("rabbit")) {
                w.setTile(l, new Rabbit());
            } else if (type.equals("burrow")) {
                w.setTile(l, new RabbitBurrow());
            } else if (type.equals("berry")) {
                w.setTile(l, new Berry());
            } else if (type.equals("wolf")) {
                w.setTile(l, new Wolf());
            } else if (type.equals("bear")) {
                //TODO: Spawn Bear-Object
            } else if (type.equals("carcass")) {
                //TODO: Spawn Carcass-Object
            }
        }
        if(type.equals("wolf")){
            Pack thePack = null;
            for(Object o :w.getEntities().keySet()){
                if(o instanceof Wolf){
                    if(thePack != null){
                        thePack.addToPack((Wolf) o);
                    }
                    else{
                        thePack = new Pack(w,(Wolf) o);
                    }
                }
            }
        }

        List<Home> rabbitBurrows = HelperMethods.availableHomes(w, "RabbitBurrow");
        Set<Object> entitiesKeys = w.getEntities().keySet();
        for (Home h : rabbitBurrows) {
            for (Object e : entitiesKeys) {
                if (!h.isAvailable()) {
                    break;
                }
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
    }

    /**
     *
     * @param w World
     * @param r Random Value
     * @return Random Empty Location in the World.
     */
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
            if (!(e instanceof Home)) {
                continue;
            }
            Home home = (Home) e;
            if (home.isAvailable() && home.getClass().getSimpleName().equals(type)) {
                availableHomes.add(home);
            }
        }
        return availableHomes;
    }

    public static Location getClosestEmptyTile(World w, int radius) {
        return getClosestEmptyTile(w, w.getCurrentLocation(), radius);
    }

    public static Location getClosestEmptyTile(World w, Location l, int radius) {
        Set<Location> oldTargetTiles = new HashSet<>();
        for (int r = 1; r <= radius; r++) {
            Set<Location> targetTiles = w.getSurroundingTiles(l, r);
            targetTiles.remove(oldTargetTiles);
            for (Location t : targetTiles) {
                if (w.isTileEmpty(t)) {
                    return t;
                }
            }
            oldTargetTiles = new HashSet<>(targetTiles);
        }
        throw new IllegalStateException("No empty tiles within set radius");
    }

    public static int getDistance(Location l1, Location l2) {
        return Math.abs(l1.getX() - l2.getX()) + Math.abs((l1.getY() - l2.getY()));
    }

    public static Set<Location> getEmptySurroundingTiles(World w, int radius) {
        return getEmptySurroundingTiles(w, w.getCurrentLocation(), radius);
    }

    public static Set<Location> getEmptySurroundingTiles(World w, Location location, int radius) {
        Set<Location> surroundingTiles = w.getSurroundingTiles(location, radius);
        Iterator<Location> it = surroundingTiles.iterator();
        while (it.hasNext()) {
            Location tile = it.next();
            if (!w.isTileEmpty(tile))
                it.remove();
        }
        return surroundingTiles;
    }

    public static Location findNearestLocationByType(World w, Location l, Set<Location> tilesInSight, String type) {
        return findNearestLocationByTypes(w, l, tilesInSight, new HashSet<>(Set.of(type)));
    }

    public static Location findNearestLocationByTypes(World w, Location l, Set<Location> tilesInSight, Set<String> types) {
        int minDistance = Integer.MAX_VALUE;
        Location minDistanceLocation = null;
        for (Location tile : tilesInSight) {
            Object tileObject = w.getTile(tile);
            if (tileObject == null) { continue; }
            boolean isOfType = types.contains(tileObject.getClass().getSimpleName());
            int distance = getDistance(l, tile);
            if (isOfType && distance < minDistance) {
                minDistance = distance;
                minDistanceLocation = tile;
            }
        }
        return minDistanceLocation;
    }

    public static Object findNearestOfObjects(World w, Set<?> objects) {
        int minDistance = Integer.MAX_VALUE;
        Object minDistanceObject = null;
        for (Object o : objects) {
            Location currL = w.getCurrentLocation();
            int distance = getDistance(currL, w.getEntities().get(o));
            if (distance < minDistance) {
                minDistance = distance;
                minDistanceObject = o;
            }
        }
        return minDistanceObject;
    }
}


