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
import java.lang.reflect.InvocationTargetException;
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

    public boolean isDead() {
        return satiation == 0 || health == 0;
    }

    private void deleteIfDead(World w) {
        if (isDead()) {
            delete(w);
        }
    }

    private int getMaxHealth() {
        return maxHealth;
    }

    protected void actionCost(int reduceBy) {
        setSatiation(satiation - reduceBy);
        setEnergy(energy - reduceBy);
    }

    public int calcNutritionAbsorbed(int nutrition) {
        return (int) Math.round(nutrition / (maxHealth / 100.0));
    }

    public void attack(World w, Animal animal) {
        if (this == animal) {
        }
        if (!animal.isAwake) {
            animal.wakeUp(w);
        }
        animal.health -= damage;
        if (animal.health <= 0) {
            animal.delete(w);
        }
    }

    public int calcMaxSpeed() {
        return (int) Math.max(1, Math.round(maxSpeed * (energy / 100.0)));
    }

    public Set<Location> calcTilesInSight(World w) {
        return w.getSurroundingTiles(w.getLocation(this), VISION_RANGE);
    }

    public void sleep(World w) {
        isAwake = false;
        if (home instanceof Hole) {
            w.remove(this);
        }
    }

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

    public void eat(World w, Edible edible) {
        int missingSatiation = calcMissingSatiation();
        int edibleNutrition = edible.getNutrition();
        setSatiation(satiation + calcNutritionAbsorbed(edibleNutrition));
        edible.setNutrition(edibleNutrition - missingSatiation);
        if (edible.getNutrition() <= 0) {
            edible.delete(w);
        }
    }

    public int calcMissingSatiation() {
        return MAX_SATIATION - satiation;
    }

    public Location getHomeLocation(World w) {
        return w.getLocation(home);
    }

    public void delete(World w) {
        Location deathL = w.getLocation(this);
        w.delete(this);
        w.setTile(deathL, new Carcass(this, false));
    }

    private void age() {
        age++;
        setMaxEnergy(BASE_MAX_ENERGY - age * AGE_MAX_ENERGY_DECREASE);
    }

    public void setHome(World w, Home home) {
        this.home = home;
        home.add(this);
    }

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

    public void emerge(World w) {
        int radius = 1;
        Location l = HelperMethods.getClosestEmptyTile(w, w.getLocation(home), radius);
        w.setCurrentLocation(l);
        w.setTile(l, this);
    }

    public void moveTo(World w, Location targetLoc) {
        moveTo(w, targetLoc, 1);
    }

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

    public void tryFindHome(World w, String type) {
        List<Home> availableBurrows = HelperMethods.availableHomes(w, type);
        if (availableBurrows.isEmpty()) { return; }
        Home burrow = availableBurrows.get(0);
        setHome(w, burrow);
    }

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

    public void randomMove(World w) {
        Set<Location> neighbours = w.getEmptySurroundingTiles(w.getLocation(this));
        Location l = (Location) neighbours.toArray()[HelperMethods.getRandom().nextInt(neighbours.size())];
        w.move(this, l);
        actionCost(1);
    }

    public void moveToMiddle(World w) {
        int x = w.getSize() / 2 - 1;
        moveTo(w, new Location(x, x)); // don't tell y
    }
}