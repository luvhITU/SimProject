package utils;

import animals.Bear;
import animals.packanimals.Fox;
import animals.packanimals.Pack;
import homes.Burrow;
import homes.Home;
import animals.Rabbit;
import animals.packanimals.Wolf;
import ediblesandflora.edibles.BerryBush;
import ediblesandflora.edibles.Carcass;
import ediblesandflora.edibles.Grass;
import itumulator.world.Location;
import itumulator.world.World;
import itumulator.executable.Program;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.*;

public abstract class HelperMethods {
    private static final Random r = new Random();
    private static List<Location> occupied = new ArrayList<>();
    private static Pack wolfPack = null;
    static int counter = 0;

    /***
     * Gets random
     * @return  r
     */
    public static Random getRandom() {
        return r;
    }

    /**
     * Parses .txt-file and returns WorldSize as Integer.
     *
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
     *
     * @param input FilePath as String to be parsed.
     * @param w     World
     * @param p     Program
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

                // if String contains "cordyceps" then the animals.Animal-Object should be infected
                if (str.contains("cordyceps") || str.contains("fungi")) {
                    isInfected = true;
                }

                // Checks if input files contains "cordyceps" or "fungi", then splits into tokens-Array
                String[] tokens = str.replaceAll("cordyceps\\s*|fungi\\s*", "").split("[\\s-,()]+");

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
                    System.out.println("Spawn Location: (" + x + "," + y + ")");
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
     *
     * @param w          World
     * @param p          Program
     * @param isInfected Boolean. Returns True if animals.Animal is infected, False if not.
     * @param type       Type of Object to be spawned.
     * @param amount     Amount of Object(s) to be spawned.
     * @param x          x-Coordinate of animals.Bear-Territory.
     * @param y          y-Coordinate of animals.Bear-Territory.
     */
    public static void spawnObject(World w, Program p, boolean isInfected, String type, int amount, int x, int y) {
        spawnObjects(w, p, isInfected, type, amount, amount, x, y);
    }

    /**
     * Spawns a certain amount of Object(s) between a Range.
     *
     * @param w          World
     * @param p          Program
     * @param isInfected Boolean. Returns True if animals.Animal is infected, False if not.
     * @param type       Type of Object to be spawned.
     * @param startRange Minimum amount of Objects to be spawned.
     * @param endRange   Maximum amount of Objects to be spawned.
     * @param x          x-Coordinate of animals.Bear-Territory.
     * @param y          y-Coordinate of animals.Bear-Territory.
     */
    public static void spawnObject(World w, Program p, boolean isInfected, String type, int startRange, int endRange, int x, int y) {
        spawnObjects(w, p, isInfected, type, startRange, endRange, x, y);
    }

    /**
     * Spawns Object(s) in the World.
     *
     * @param w          World
     * @param p          Program
     * @param isInfected Boolean. Returns True if animals.Animal is infected, False if not.
     * @param type       Type of Object to be spawned.
     * @param startRange Minimum amount of Objects to be spawned.
     * @param endRange   Maximum amount of Objects to be spawned.
     * @param x          x-Coordinate of animals.Bear-Territory.
     * @param y          y-Coordinate of animals.Bear-Territory.
     */
    private static void spawnObjects(World w, Program p, boolean isInfected, String type, int startRange, int endRange, int x, int y) {
        int rValue = r.nextInt((endRange + 1) - startRange) + startRange;
        if (startRange != endRange) {
            System.out.println("Range Value: " + rValue);
        }

        for (int i = 0; i < rValue; i++) {
            Location l = getRandomEmptyLocation(w, r);
            occupied.add(l);

            if (type.equals("grass")) {
                w.setTile(l, new Grass());
            } else if (type.equals("rabbit")) {
                w.setTile(l, new Rabbit());
            } else if (type.equals("burrow")) {
                w.setTile(l, new Burrow(l, Config.Rabbit.MAX_BURROW_OCCUPANTS, "Rabbit"));
            } else if (type.equals("berry")) {
                w.setTile(l, new BerryBush());
            } else if (type.equals("wolf")) {
                Wolf newWolf = new Wolf();
                w.setTile(l, newWolf);
                if(wolfPack == null){
                    wolfPack = new Pack();
                    int PackSize = Math.max(rValue, Config.Wolf.MAX_PACK_SIZE);
                    Burrow newBurrow = new Burrow(l,PackSize,newWolf.getClass().getSimpleName());
                    w.add(wolfPack);
                    w.setTile(l,newBurrow);
                    wolfPack.setPackHome(w,newBurrow);
                }
                wolfPack.add(w,newWolf);
            } else if (type.equals("bear")) {
                if (!(x == -1 && y == -1)) {
                    w.setTile(new Location(x, y), new Bear());
                } else {
                    w.setTile(l, new Bear());
                }
            } else if (type.equals("carcass")) {
                w.setTile(l, new Carcass(isInfected));
            } else if (type.equals("fox")) {
                w.setTile(l, new Fox());
            }
        }
        wolfPack = null;
    }
    // ONLY USED TO VISUALIZE BEAR TERRITORY
//        if (!(x == -1 && y == -1)) {
//            w.setTile(new Location(x, y), new BearTerritory());
//        }
//    }

    /**
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

    /***
     * Searches the World for homes that still has room of a specific type
     * @param w     World
     * @param species  Animal species the home houses.
     * @return      Arraylist< Home >
     */
    public static ArrayList<Home> availableHomes(World w, String species) {
        Map<Object, Location> entities = w.getEntities();
        ArrayList<Home> availableHomes = new ArrayList<>();
        for (Object e : entities.keySet()) {
            if (!(e instanceof Home)) {
                continue;
            }
            Home home = (Home) e;
            if (!home.isFull() && home.getAllowedSpecies().equals(species)) {
                availableHomes.add(home);
            }
        }
        return availableHomes;
    }

    /***
     * Returns the closest empty tile within the given radius
     * @param w         World
     * @param l         Searching Point Location
     * @param radius    Radius in int
     * @return          Tile that does not contain a blocking object
     */
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
        return null;
    }

    /***
     * Returns int of distance between 2 locations
     * @param l1    First Location
     * @param l2    Second Location
     * @return      Distance in int
     */
    public static int getDistance(Location l1, Location l2) {
        return Math.abs(l1.getX() - l2.getX()) + Math.abs((l1.getY() - l2.getY()));
    }

    /***
     * Returns set of empty surrounding locations
     * @param w         World
     * @param radius    Radius in int
     * @return          Set< Location >
     */
    public static Set<Location> getEmptySurroundingTiles(World w, int radius) {
        return getEmptySurroundingTiles(w, w.getCurrentLocation(), radius);
    }

    /***
     * Gets tiles within radius that does not contain a blocking object
     * @param w         World
     * @param location  Searching Point Location
     * @param radius    Radius in int
     * @return          Set< Location >
     */
    public static Set<Location> getEmptySurroundingTiles(World w, Location location, int radius) {
        Set<Location> surroundingTiles = w.getSurroundingTiles(location, radius);
        surroundingTiles.removeIf(tile -> !w.isTileEmpty(tile));
        return surroundingTiles;
    }

    /***
     * Returns location of nearest instance of object
     * @param w             World
     * @param l             Current Location
     * @param tilesInSight  Set of location
     * @param type          Type of object
     * @return              Location
     */
    public static Location findNearestLocationByType(World w, Location l, Set<Location> tilesInSight, String type) {
        return findNearestLocationByTypes(w, l, tilesInSight, new HashSet<>(Set.of(type)));
    }

    /***
     * Returns location of nearest instance of one of the set of object
     * @param w             World
     * @param l             Current Location
     * @param tilesInSight  Set of location
     * @param types         Type of object
     * @return              Nearest location that is in the set of types
     */
    public static Location findNearestLocationByTypes(World w, Location l, Set<Location> tilesInSight, Set<String> types) {
        int minDistance = Integer.MAX_VALUE;
        Location minDistanceLocation = null;
        for (Location tile : tilesInSight) {
            Object tileObject = w.getTile(tile);
            if (tileObject == null) {
                continue;
            }
            boolean isOfType = types.contains(tileObject.getClass().getSimpleName());
            int distance = getDistance(l, tile);
            if (isOfType && distance < minDistance) {
                minDistance = distance;
                minDistanceLocation = tile;
            }
        }
        return minDistanceLocation;
    }

    /***
     * Finds nearest instance of input object using current position of object
     * @param w         World
     * @param objects   Object
     * @return          Nearest instance of input object
     */
    public static Object findNearestOfObjects(World w, Set<?> objects) {
        return findNearestOfObjects(w, w.getCurrentLocation(), objects);
    }

    /***
     * Finds nearest object of types
     * @param w         World
     * @param l         Location
     * @param objects   (Set of) object(s)
     * @return          Nearest instance of one of the objects
     */
    public static Object findNearestOfObjects(World w, Location l, Set<?> objects) {
        int minDistance = Integer.MAX_VALUE;
        Object minDistanceObject = null;
        for (Object o : objects) {
            Location oLoc = (o instanceof Location) ? (Location) o : w.getLocation(o);
            int distance = getDistance(l, oLoc);
            if (distance < minDistance) {
                minDistance = distance;
                minDistanceObject = o;
            }
        }
        return minDistanceObject;
    }
    /***
     * Used to invoke methods with shared names but no shared inheritance for the method with input name
     * @param o             Object that the method is invoked on
     * @param methodName    Name of the method that is invoked
     * @param w             World
     */
    public static void invokeMethod(Object o, String methodName,World w) {
        try {
            Method m = o.getClass().getMethod(methodName,World.class);
            m.invoke(o,w);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}