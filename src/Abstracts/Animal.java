package Abstracts;

import Places.Hole;
import Places.Home;
import MapComponents.Carcass;
import Helper.HelperMethods;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;
import java.util.List;
import java.util.*;

public abstract class Animal extends SimComponent implements Actor, DynamicDisplayInformationProvider {


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
        satiation = MAX_SATIATION;
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
        // If a day has passed since last age increase, age.
        if (stepAge % World.getTotalDayDuration() == 0) {
            age();
        }
        // While sleeping, increase energy every step.
        if (!isAwake) {
            setEnergy(energy + STEP_SLEEP_ENERGY_INCREASE);
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
        StringBuilder imageKeyBuilder = new StringBuilder(getType().toLowerCase());
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
        if (home instanceof Hole) {
            w.remove(this);
        }
    }

    /***
     * Sets isAwake to true and uses emerge()
     * @param w World
     */
    public void wakeUp(World w) {
        isAwake = true;
        if (home instanceof Hole) {
            emerge(w);
        }
    }

    protected void setSatiation(int satiation) {
        this.satiation = Math.max(0, Math.min(MAX_SATIATION, satiation));
    }

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
        edible.setNutrition(edibleNutrition - missingSatiation);
        if (edible.getNutrition() <= 0) {
            edible.delete(w);
        }
    }

    /***
     * Returns the difference of MAX_SATIATION and satiation in int
     * @return  int
     */
    public int calcMissingSatiation() {
        return MAX_SATIATION - satiation;
    }

    /***
     * Getter for location of Home
     * @param w Wolrd
     * @return  Location
     */
    public Location getHomeLocation(World w) {
        return w.getLocation(home);
    }

    /***
     * Deletes object and places a Carcass on location
     * @param w
     */
    public void delete(World w) {
        Location deathL = w.getLocation(this);
        w.delete(this);
        w.setTile(deathL, new Carcass(this, false));
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
    }

    /***
     * Moves towards location of Home and if it on Home then the object gets removed
     * @param w World
     */
    public void goHome(World w) {
        if (w.getLocation(this).equals(getHomeLocation(w))) {
            sleep(w);
        } else {
            moveTo(w, getHomeLocation(w));
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
//        int currDistToTargetLoc = Helper.HelperMethods.getDistance(w.getLocation(this), targetLoc);
//        int newDistToTargetLoc = Helper.HelperMethods.getDistance(bestMove, targetLoc);
//        if (newDistToTargetLoc < currDistToTargetLoc) {

            w.move(this, bestMove);
            actionCost(speed);
//        }
    }

    /***
     * Finds a home if one is available
     * @param w     World
     * @param type  String
     */
    public void tryFindHome(World w, String type) {
        List<Home> availableBurrows = HelperMethods.availableHomes(w, type);
        if (availableBurrows.isEmpty()) { return; }
        Home burrow = availableBurrows.get(0);
        setHome(w, burrow);
    }

    /***
     * Digs a burrow if it possible and sets Home to the new burrow
     * @param w         World
     * @param burrow    Home
     */
    public void digBurrow(World w, Home burrow) {
        Location target;
        Location currL = w.getLocation(this);
        if (!(w.containsNonBlocking(currL) && w.getNonBlocking(currL) instanceof Home)) {
            target = currL;
        } else {
            Set<Location> neighbours = HelperMethods.getEmptySurroundingTiles(w, currL, 5);
            neighbours.removeIf(n -> w.containsNonBlocking(n) && w.getNonBlocking(n) instanceof Home);
            target = HelperMethods.findNearestLocationByType(w, w.getLocation(this), neighbours, "Location");
        }

        w.setTile(target, burrow);
        burrow.add(this);
        home = burrow;
    }

//    public void reproduce(World w, Abstracts.Animal partner) {
//        Location l = Helper.HelperMethods.getClosestEmptyTile(w, 1);
//        Abstracts.Animal lilBaby = null;
//        try {
//            lilBaby = this.getClass().getDeclaredConstructor().newInstance();
//        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
//                 NoSuchMethodException ignore) {
//        }
//        w.setTile(l, lilBaby);
//        resetReproductionCooldown();
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
            targetInRange = isTargetInRange(w, target);
        }

        if (targetInRange) {
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
        Location midLocation = new Location(x, x); // don't tell y
    }
}