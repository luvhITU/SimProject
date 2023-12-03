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

public abstract class Animal extends Edible implements Actor, DynamicDisplayInformationProvider {


    private static final int MATURITY_AGE = 3;
    private static final int BASE_MAX_ENERGY = 100;
    private static final int AGE_ENERGY_DECREASE = 5;
    private static final int ACTION_COST = 2;
    private static final int VISION_RANGE = 5;
    private final int damage;
    private final Set<String> diet;
    private final String mode;
    private int reproductionCooldown;
    Set<Location> territory;
    private boolean isAwake;
    private Home home;
    private int energy;
    private int health;
    private int maxHealth;
    private int maxEnergy;
    private int stepAge;
    private int age;
    private Set<Location> tilesInSight;
    private final int selfPreservationLevel;

    public Animal(Set<String> diet, int damage, int health, int selfPreservationLevel) {
        super(health);
        this.diet = diet;
        energy = BASE_MAX_ENERGY;
        maxEnergy = BASE_MAX_ENERGY;
        stepAge = 0;
        age = 0;
        this.damage = damage;
        this.isAwake = true;
        home = null;
        tilesInSight = new HashSet<>();
        mode = null;
        // If your animal should reproduce, make this reduce per step in its act() override
        reproductionCooldown = MATURITY_AGE * World.getTotalDayDuration();
        this.selfPreservationLevel = selfPreservationLevel;
        this.health = health;
        maxHealth = health;
    }

    public void act(World w) {
        System.out.println("Energy: " + energy);
        stepAge++;
        if (stepAge % World.getTotalDayDuration() == 0) {
            age();
        }
        if (isAwake) {
            aiPackage(w);
        }
        actionCost();
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, buildImageString());
    }

    private String buildImageString() {
        String imageString = this.getClass().getSimpleName().toLowerCase();
        if (!getIsMature()) {
            imageString += "-small";
        }
        if (!isAwake) {
            imageString += "-sleeping";
        }
        return imageString;
    }

    public void reduceReproductionCooldown() {
        reproductionCooldown--;
    }

    public boolean getIsDead(World w) {
        return health == 0;
    }

    public void tryRandomMove(World w) {
        Set<Location> neighbours = w.getEmptySurroundingTiles();
        if (neighbours.isEmpty()) {
            return;
        }

        Location l = (Location) neighbours.toArray()[HelperMethods.getRandom().nextInt(neighbours.size())];
        w.move(this, l);
        actionCost();
    }

    public int calcNutritionAbsorbed(Edible edible) {
        return (int) Math.round(edible.getNutrition() / (health / 100.0));
    }

    public void attack(World w, Animal animal) {
        animal.wakeUp(w);
        animal.health -= damage;
    }

    public void setTerritory(Set<Location> territory) {
        this.territory = territory;
    }

    public int getDamage() {
        return damage;
    }

    public void setTilesInSight(Set<Location> tilesInSight) {
        this.tilesInSight = tilesInSight;
    }

    public Set<String> getDiet() {
        return diet;
    }

    public boolean getIsAwake() {
        return isAwake;
    }

    public void sleep(World w) {
        isAwake = false;
    }

    public void wakeUp(World w) {
        isAwake = true;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(maxEnergy, energy));
    }

    public void actionCost() {
        setEnergy(energy - ACTION_COST);
    }

    private void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = Math.max(30, maxEnergy);
    }

    public void eat(World w, Edible edible) {
        int missingEnergy = maxEnergy - energy;
        int edibleNutrition = edible.getNutrition();
        setEnergy(energy + edibleNutrition);
        edible.setNutrition(edibleNutrition - missingEnergy);
        edible.delete(w);
        setEnergy(energy + calcNutritionAbsorbed(edible));
    }

    private void age() {
        age++;
        setMaxEnergy(BASE_MAX_ENERGY - age * AGE_ENERGY_DECREASE);
    }

    public void setHome(World w, Location l, Home home) {
        w.setTile(l, home);
        this.home = home;
        home.add(this);
    }

    public void setHome(World w, Home home) {
        this.home = home;
        home.add(this);
    }


    public Home getHome() {
        return home;
    }

    public void hide(World w) {
        w.remove(this);
        sleep(w);
    }

    public boolean getIsMature() {
        return age >= MATURITY_AGE;
    }

    public void emerge(World w) {
        int radius = 3;
        Location l = HelperMethods.getClosestEmptyTile(w, w.getLocation(home), radius);
        wakeUp(w);
        w.setCurrentLocation(w.getLocation((home)));
        w.setTile(l, this);
    }

    public void moveTo(World w, Location l) {
        Location currL = w.getCurrentLocation();
        Set<Location> neighbours = w.getEmptySurroundingTiles();

        int minDistance = Integer.MAX_VALUE;
        Location bestMove = null;
        for (Location n : neighbours) {
            int nDistance = HelperMethods.getDistance(currL, l);
            if (nDistance < minDistance) {
                minDistance = nDistance;
                bestMove = n;
            }
        }

        w.move(this, bestMove);
        actionCost();
    }

    public void findHome(World w, String type) {
        List<Home> availableBurrows = HelperMethods.availableHomes(w, type);
        if (availableBurrows.isEmpty()) {
            throw new IllegalStateException("No homes available");
        }
        Home burrow = availableBurrows.get(0);
        setHome(w, burrow);
    }

    public void digBurrow(World w, Home burrow) {
        Location l = w.getCurrentLocation();
        Object nonBlocking = null;
        if (w.containsNonBlocking(l)) {
            nonBlocking = w.getNonBlocking(l);

            if (nonBlocking instanceof Home) {
                throw new IllegalStateException("There already exists a home at this location");
            }
            w.delete(nonBlocking);
        }
        setHome(w, l, burrow);
        actionCost();
    }

    public void resetReproductionCooldown() {
        reproductionCooldown = World.getTotalDayDuration() * 2;
    }

    public void reproduce(World w, Animal partner) {
        Location l = HelperMethods.getClosestEmptyTile(w, 1);
        Animal lilBaby = null;
        try {
            lilBaby = this.getClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException ignore) {
        }
        w.setTile(l, lilBaby);
        resetReproductionCooldown();
        partner.resetReproductionCooldown();
        actionCost();
    }

    public Location getLocation(World w, Object object) {
        return w.getEntities().get(object);
    }

    private boolean isHorny(World w) {
        return energy > 70 && reproductionCooldown <= 0;
    }

    private Location getFleeToTile(World w, Location predatorLocation) {
        List<Location> neighbours = new ArrayList<>(w.getEmptySurroundingTiles());
        neighbours.sort(Comparator.comparingInt(n -> HelperMethods.getDistance(n, predatorLocation)));
        return neighbours.get(0);

    }

    private Edible findClosestEdible(World w) {
        return (Edible) w.getTile(HelperMethods.findNearestLocationByTypes(w, w.getCurrentLocation(), tilesInSight, diet));
    }

    // Needs an overrite in wolf, to not detect animals in pack.
    private Animal findClosestPredator(World w) {
        Set<Animal> predators = findPredators(w);
        return (Animal) HelperMethods.findNearestOfObjects(w, predators);
    }

    private Animal findLargestPredator(World w) {
        Set<Animal> predators = findPredators(w);
        Animal largestPredator = null;
        int maxMaxHealth = 0;

        for (Animal p : predators) {
            int pMaxHealth = p.maxHealth;
            if (pMaxHealth > maxMaxHealth) {
                maxMaxHealth = pMaxHealth;
                largestPredator = p;
            }
        }

        return largestPredator;
    }

    private boolean isLargerPredatorInSight(World w) {
        return health < findLargestPredator(w).health;
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
            if (animal.diet.contains(thisType) && !animal.mode.equals("sleep")) {
                predators.add(animal);
            }
        }
        return predators;
    }

    private Animal findClosestSameSpecies(World w) {
        return (Animal) w.getTile(HelperMethods.findNearestLocationByType(w, w.getCurrentLocation(), tilesInSight, getClass().getSimpleName()));
    }

    private Animal findClosestPartner(World w) {
        Set<Location> partnerLocations = new HashSet<>();
        for (Location n : tilesInSight) {
            Object entity = w.getTile(n);
            if (entity == null || !getClass().equals(entity.getClass())) {
                continue;
            }
            Animal potentialPartner = (Animal) entity;
            if (potentialPartner.isHorny(w)) {
                partnerLocations.add(n);
            }
        }
        if (partnerLocations.isEmpty()) {
            return null;
        }
        return (Animal) w.getTile(HelperMethods.findNearestLocationByType(w, w.getCurrentLocation(), partnerLocations, getClass().getSimpleName()));
    }


    private String getMode(World w) {
        if (!isAwake) {
            return "stay";
        }
        String mode = "flee";
        String thisType = getClass().getSimpleName();
        boolean isCannibalistic = diet.contains(thisType);
        boolean isPredatorInSight = isPredatorInSight(w);
        boolean isHungry = energy < 50;
        int activeSelfPreservationLevel = (isHungry && selfPreservationLevel == 1
                || selfPreservationLevel == 0) ? 0 : selfPreservationLevel;
        boolean isTired = w.isNight() | energy < 20;
        Edible closestEdible = findClosestEdible(w);
        Animal closestPredator = findClosestPredator(w);
        Animal closestPartner = null;
        if (isHorny(w)) {
            closestPartner = findClosestPartner(w);
        }

        if (isPredatorInSight) {
            if (activeSelfPreservationLevel == 0 || activeSelfPreservationLevel == 2 && !isLargerPredatorInSight(w)) {
                mode = "attack";
            }
        } else if (w.getEmptySurroundingTiles().isEmpty()) {
            mode = "stay";
        } else if (isTired) {
            mode = "sleep";
        } else if (closestPartner != null) {
            mode = "reproduce";
        } else {
            mode = "eat";
        }
        return mode;
    }

    private void aiPackage(World w) {
        String mode = getMode(w);
        Location targetLocation = null;
        switch (mode) {
            case "stay": {
                System.out.println("STAY");
                return;
            }
            case "flee": {
                System.out.println("FLEE");
                Location closestPredatorLocation = w.getEntities().get(findClosestPredator(w));
                w.move(this, getFleeToTile(w, closestPredatorLocation));
                break;
            }
            case "attack": {
                System.out.println("ATTACK");
                // find largest predator or non predator
                // if it is within 1 block radius, attack. else move to it
                Animal largestPredator = findLargestPredator(w);
                targetLocation = getLocation(w, largestPredator);
                if (w.getSurroundingTiles().contains(targetLocation)) {
                    attack(w, largestPredator);
                    return;
                }
            }
            case "sleep": {
                System.out.println("SLEEP");
                targetLocation = getLocation(w, home);
                if (w.getCurrentLocation().equals(targetLocation)) {
                    sleep(w);
                    return;
                }
            }
            case "reproduce": {
                System.out.println("REPRODUCE");
                Animal partner = findClosestPartner(w);
                targetLocation = getLocation(w, partner);
                if (w.getSurroundingTiles().contains(targetLocation) && !w.getEmptySurroundingTiles().isEmpty()) {
                    reproduce(w, partner);
                    return;
                }
            }
            case "eat": {
                System.out.println("EAT");
                Edible edible = findClosestEdible(w);
                targetLocation = getLocation(w, edible);
                boolean isNonBlocking = edible instanceof NonBlocking;
                if (isNonBlocking && w.getCurrentLocation().equals(targetLocation)
                        || w.getSurroundingTiles().contains(targetLocation)) {
                    eat(w, edible);
                    return;
                }
            }
        }
        moveTo(w, targetLocation);
    }
}