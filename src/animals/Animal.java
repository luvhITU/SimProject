package animals;

import ediblesandflora.edibles.Edible;
import homes.Burrow;
import homes.Home;
import ediblesandflora.edibles.Carcass;
import utils.HelperMethods;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class Animal implements Actor, DynamicDisplayInformationProvider {


    protected static final int MATURITY_AGE = 3;
    protected static final int BASE_MAX_ENERGY = 100;
    protected static final int MAX_SATIATION = 100;
    protected static final int AGE_MAX_ENERGY_DECREASE = 5;
    protected static final int STEP_SLEEP_ENERGY_INCREASE = 5;

    protected static final int VISION_RANGE = 4;

    public final int maxHealth;
    protected int maxEnergy;
    protected int energy;
    protected int satiation;
    protected int health;
    protected final int damage;
    protected final int maxSpeed;
    protected final int matingCooldownDays;
    protected int stepAgeWhenMated;
    protected final Set<String> diet;
    protected Home home;
    protected boolean isAwake;
    protected boolean isBreedable;
    protected int stepAge;
    protected int age;
    protected Set<Location> tilesInSight;

    /***
     * Initializes input variables
     * @param diet                  Set< String >
     * @param damage                int
     * @param maxHealth             int
     * @param maxSpeed              int
     * @param matingCooldownDays    int
     */
    public Animal(Set<String> diet, int damage, int maxHealth, int maxSpeed, int matingCooldownDays) {
        this.diet = diet;
        this.damage = damage;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.maxSpeed = maxSpeed;
        this.matingCooldownDays = matingCooldownDays;

        maxEnergy = BASE_MAX_ENERGY;
        satiation = 70;
        energy = BASE_MAX_ENERGY;
        stepAge = 0;
        stepAgeWhenMated = 0;
        age = 0;
        isAwake = true;
        isBreedable = matingCooldownDays != 0;
        home = null;
    }

    /***
     * See super
     * @param w providing details of the position on which the actor is currently located and much more.
     */
    public void act(World w) {
        stepAge++;
        // If a day has passed, age.
        if (stepAge % World.getTotalDayDuration() == 0) {
            age();
        }

        if (!isAwake) {
            // Increase energy every stepe while sleeping.
            setEnergy(energy + STEP_SLEEP_ENERGY_INCREASE);
            // If it's morning, wake up.
            if (w.getCurrentTime() == 0) {
                wakeUp(w);
            }
        }
        if (isAwake) {
            tilesInSight = calcTilesInSight(w);
        }

    }

    // Implement canMateFunction

    /***
     * See super
     * @return  DisplayInformation
     */
    @Override
    public DisplayInformation getInformation() {
        StringBuilder imageKeyBuilder = new StringBuilder(this.getClass().getSimpleName().toLowerCase());
        if (!getIsMature()) {
            imageKeyBuilder.append("-small");
        }
        if (!isAwake) {
            imageKeyBuilder.append("-sleeping");
        }
        return new DisplayInformation(Color.magenta, imageKeyBuilder.toString());
    }

    /***
     * Checks if object is dead
     * @return  Boolean
     */
    public boolean isDead() {
        return satiation == 0 || health == 0;
    }

    private void deleteIfDead(World w) {
        if (isDead()) {
            delete(w);
        }
    }
    //Should it be Protected?
    private int getMaxHealth() {
        return maxHealth;
    }

    /***
     * Changes both satiation and energy by input int
     * @param reduceBy  int
     */
    protected void actionCost(int reduceBy) {
        setSatiation(satiation - reduceBy);
        setEnergy(energy - reduceBy);
    }

    /***
     * Returns int for nutrition absorbed based on maxHealth
     * @param nutrition int
     * @return          int
     */
    public int calcNutritionAbsorbed(int nutrition) {
        return (int) Math.round(nutrition / (maxHealth / 100.0));
    }

    /***
     * Changes health of animal and deletes the animal if health gets below 0
     * @param w         World
     * @param animal    Animal
     */
    public void attack(World w, Animal animal) {
        if (this == animal) {
            //Does nothing, should everything else be in this?
        }
        if (!animal.isAwake) {
            animal.wakeUp(w);
        }
        animal.health -= damage;
        if (animal.health <= 0) {
            animal.delete(w);
        }
    }

    /***
     * Returns int speed which is based on maxSpeed and energy
     * @return  int
     */
    public int calcMaxSpeed() {
        return (int) Math.max(1, Math.round(maxSpeed * (energy / 100.0)));
    }

    /***
     * Uses int VISION_RANGE to get tiles that is within that radius
     * @param w World
     * @return  Set< Location >
     */
    public Set<Location> calcTilesInSight(World w) {
        return w.getSurroundingTiles(w.getLocation(this), VISION_RANGE);
    }

    /***
     * Sets isAwake to false and removes object if it is on top of Hole
     * @param w World
     */
    public void sleep(World w) {
        isAwake = false;
        if (home instanceof Burrow) {
            w.remove(this);
        }
    }

    /***
     * Sets isAwake to true and uses emerge()
     * @param w World
     */
    public void wakeUp(World w) {
        isAwake = true;
        if (home instanceof Burrow) {
            emerge(w);
        }
    }

    public static int getMaxSatiation() {
        return MAX_SATIATION;
    }

    protected void setSatiation(int satiation) {
        this.satiation = Math.max(0, Math.min(MAX_SATIATION, satiation));
    }

    /***
     * Getter for satiation
     * @return  int
     */
    public int getSatiation() {
        return satiation;
    }

    /***
     * Getter for energy
     * @return  int
     */
    public int getEnergy(){ return energy;}
    private void setMaxEnergy(int maxEnergy) {
        int minMaxEnergy = 30;
        this.maxEnergy = Math.max(minMaxEnergy, maxEnergy);
    }

    /***
     * Changes nutrition based on how much missing Satiation and deletes if nutrition gets to or below 0
     * @param w         World
     * @param edible    Edible
     */
    public void eat(World w, Edible edible) {
        int missingSatiation = calcMissingSatiation();
        int edibleNutrition = edible.getNutrition();
        setSatiation(satiation + calcNutritionAbsorbed(edibleNutrition));
        edible.reduceNutritionBy(missingSatiation);
        if (edible.getNutrition() <= 0) {
            edible.delete(w);
        }
    }

    /***
     * Returns the difference of MAX_SATIATION and satiation in int
     * @return  int
     */
    public int calcMissingSatiation() { return MAX_SATIATION - satiation; }

    /***
     * Getter for location of Home
     * @return  Location
     */
    public Location getHomeLocation() {
        return home.getLocation();
    }

    /***
     * Deletes object and places a Carcass on location
     * @param w World
     */
    public void delete(World w) {
        if (home != null) {
            home.remove(this);
        }
        Location currL = w.getLocation(this);
        w.delete(this);
        w.setTile(currL, new Carcass(this, false));
    }

    private void age() {
        age++;
        setMaxEnergy(BASE_MAX_ENERGY - age * AGE_MAX_ENERGY_DECREASE);
    }

    /***
     * Initializes Home to input Home
     * @param w     Wolrd
     * @param home  Home
     */
    public void setHome(World w, Home home) {
        this.home = home;
        home.add(this);
        if (!w.contains(home)) {
            w.add(home);
        }
    }

    /***
     * Moves towards location of Home and if it on Home then the object gets removed
     * @param w World
     */
    public void goHome(World w) {
        if (w.getLocation(this).equals(getHomeLocation())) {
            sleep(w);
        } else {
            moveTo(w, getHomeLocation());
        }
    }


    private boolean getIsMature() {
        return age >= MATURITY_AGE;
    }

    protected void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(maxEnergy, energy));
    }

    /***
     * Sets location of object within a radius of 1 of the Home location
     * @param w World
     */
    public void emerge(World w) {
        int radius = 1;
        Location l = HelperMethods.getClosestEmptyTile(w, w.getLocation(home), radius);
        w.setCurrentLocation(l);
        w.setTile(l, this);
    }

    /***
     * Moves object towards input Location
     * @param w         World
     * @param targetLoc Location
     */
    public void moveTo(World w, Location targetLoc) {
        moveTo(w, targetLoc, 1);
    }

    /***
     * Moves object towards input Location where speed changes how much it moves pr. tick
     * @param w         World
     * @param targetLoc Location
     * @param speed     int
     */
    public void moveTo(World w, Location targetLoc, int speed) {
        Set<Location> neighbours = HelperMethods.getEmptySurroundingTiles(w, w.getLocation(this), speed);
        Location bestMove = (Location) HelperMethods.findNearestOfObjects(w, targetLoc, neighbours);
//        int currDistToTargetLoc = utils.HelperMethods.getDistance(w.getLocation(this), targetLoc);
//        int newDistToTargetLoc = utils.HelperMethods.getDistance(bestMove, targetLoc);
//        if (newDistToTargetLoc < currDistToTargetLoc) {

            w.move(this, bestMove);
            actionCost(speed);
//        }
    }

    /***
     * Tries to find and a home if one is available. If not, and location is burrow-able, then it burrows a burrow
     * @param w World
     * @param maxOccupants String
     */
    public void tryFindOrDigBurrow(World w, int maxOccupants) {
        tryOccupyExistingHomes(w, "Burrow");
        if (home == null && canBurrowHere(w)) {
            burrow(w, maxOccupants);
        }
    }
    /***
     * Finds a home if one is available
     * @param w     World
     * @param type  String
     */
    private void tryOccupyExistingHomes(World w, String type) {
        if (home != null) { throw new IllegalStateException("Animal already has a home"); }
        List<Home> availableHomes = HelperMethods.availableHomes(w, getClass().getSimpleName());
        if (availableHomes.isEmpty()) { return; }
        Home home = availableHomes.get(0);
        setHome(w, home);
    }

    /***
     * Digs a burrow sets home to the new burrow
     * @param w         World
     * @param maxOccupants int
     */
    public void burrow(World w, int maxOccupants) {
        if (!canBurrowHere(w)) { throw new IllegalArgumentException("Cannot burrow at this location"); }
        if (home != null) { throw new IllegalStateException("Animal already has a home"); }

        Burrow burrow = new Burrow(w.getLocation(this), maxOccupants, getClass().getSimpleName());
        setHome(w, burrow);
        w.setTile(burrow.getLocation(), burrow);
    }

    protected boolean canBurrowHere(World w) {
        Location currL = w.getLocation(this);
        // Returns false if animal is standing on an existing home
        return (!(w.containsNonBlocking(currL) && w.getNonBlocking(currL) instanceof Home));
    }

//    public void reproduce(World w, animals.Animal partner) {
//        Location l = utils.HelperMethods.getClosestEmptyTile(w, 1);
//        animals.Animal lilBaby = null;
//        try {
//            lilBaby = this.getClass().getDeclaredConstructor().newInstance();
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
//                 NoSuchMethodException ignore) {
//        }
//        w.setTile(l, lilBaby);
//        partner.resetReproductionCooldown();
//        actionCost(6, 9);
//    }


    private boolean canMate(World w) {
        return getIsMature() && stepAgeWhenMated - stepAge <= matingCooldownDays * World.getTotalDayDuration();
    }

    protected void flee(World w, Location predatorLocation) {
        int speed = calcMaxSpeed();
        List<Location> neighbours = new ArrayList<>(HelperMethods.getEmptySurroundingTiles(w, w.getLocation(this), speed));
        if (neighbours.isEmpty()) {
            return;
        }

        neighbours.sort(Comparator.comparingInt(n -> HelperMethods.getDistance(n, predatorLocation)));
        w.move(this, neighbours.get(neighbours.size() - 1));
        actionCost(speed);
    }

    protected void hunt(World w, Object target) {
        Location targetLoc = w.getLocation(target);
        boolean targetInRange = isTargetInRange(w, target);
        boolean isAnimal = target instanceof Animal;

        if (!targetInRange) {
            int speed = (isAnimal) ? calcMaxSpeed() : 1;
            moveTo(w, targetLoc, speed);
        } else  {
            if (isAnimal) {
                attack(w, (Animal) target);
            } else {
                eat(w, (Edible) target);
            }
        }
    }

    private boolean isTargetInRange(World w, Object target) {
        Location targetLoc = w.getLocation(target);
        if (target instanceof NonBlocking) {
            return w.getLocation(this).equals(targetLoc);
        }
        return w.getSurroundingTiles(w.getLocation(this)).contains(targetLoc);
    }

    protected Set<Object> findEdibles(World w) {
        Set<Object> edibles = new HashSet<>();
        for (Location l : tilesInSight) {
            Object o = w.getTile(l);
            if (o != null && diet.contains(o.getClass().getSimpleName())) {
                if (o instanceof Edible && ((Edible) o).isEdible() || o instanceof Animal)
                    edibles.add(o);
            }
        }
        return edibles;
    }

    protected Object findClosestEdible(World w) {
        return HelperMethods.findNearestOfObjects(w, w.getLocation(this), findEdibles(w));
    }

    // Needs an override in wolf, to not detect animals in pack.
    protected Animal findClosestPredator(World w) {
        Set<Animal> predators = findPredators(w);
        return (Animal) HelperMethods.findNearestOfObjects(w, w.getLocation(this), predators);
    }

    private boolean isPredatorInSight(World w) {
        return !findPredators(w).isEmpty();
    }

    private Set<Animal> findPredators(World w) {
        Set<Animal> predators = new HashSet<>();
        String thisType = getClass().getSimpleName();
        for (Location tile : tilesInSight) {
            Object obj = w.getTile(tile);
            if (!(obj instanceof Animal)) {
                continue;
            }
            Animal animal = (Animal) obj;
            if (animal.diet.contains(thisType)) {
                predators.add(animal);
            }
        }
        return predators;
    }

    /***
     * Moves to a random empty surrounding tile
     * @param w World
     */
    public void randomMove(World w) {
        Set<Location> neighbours = w.getEmptySurroundingTiles(w.getLocation(this));
        Location l = (Location) neighbours.toArray()[HelperMethods.getRandom().nextInt(neighbours.size())];
        w.move(this, l);
        actionCost(1);
    }

    /***
     * Does nothing but calculates center of the map
     * @param w World
     */
    public void moveToMiddle(World w) {
        int x = w.getSize() / 2 - 1;
        moveTo(w, new Location(x, x)); // don't tell y
    }

    /***
     * Getter for home
     * @return  Home
     */
    public Home getHome() {
        return home;
    }
}