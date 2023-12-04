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
    private static final int MAX_SATIATION = 100;
    private static final int AGE_ENERGY_DECREASE = 5;
    private static final int ACTION_COST = 2;
    private static final int VISION_RANGE = 5;
    private final int damage;
    private Set<String> diet;
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
    private final int aggression;
    private int satiation;
    private boolean isDead;
    private int speed;

    public Animal(Set<String> diet, int damage, int health, int aggression, int speed) {
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
        mode = "";
        reproductionCooldown = MATURITY_AGE * World.getTotalDayDuration();
        this.aggression = aggression;
        this.health = health;
        maxHealth = health;
        satiation = MAX_SATIATION;
        isDead = false;
        this.speed = speed;
    }

    public void act(World w) {
        if (satiation == 0) { isDead = true; }
        if (isDead) { return; }
        stepAge++;
        if (stepAge % World.getTotalDayDuration() == 0) {
            age();
        }

        if (energy < 20) {
            System.out.println("tired");
        }
        if (isAwake) {
            aiPackage(w);
        }
        if (!isAwake) {
            actionCost(1, -8);
        }

    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.magenta, buildImageKey());
    }

    private String buildImageKey() {
        String imageKey = null;
        if (isDead) {
            imageKey = (getNutrition() > 100) ? "carcass" : "carcass-small";
        } else {
            imageKey = getClass().getSimpleName().toLowerCase();
            imageKey += (!getIsMature()) ? "-small" : "";
            imageKey += (!isAwake) ? "-sleeping" : "";
        }
        return imageKey;
    }

    public void reduceReproductionCooldown() {
        reproductionCooldown--;
    }

    public int calcNutritionAbsorbed(int nutrition) {
        return (int) Math.round(nutrition / (maxHealth / 100.0));
    }

    public void attack(World w, Animal animal) {
        animal.wakeUp(w);
        animal.health -= damage;
        if (animal.health <= 0) {
            animal.die();
        }
    }

    public void die() {
        isDead = true;
        diet = new HashSet<>();
    }

    public int getSpeed() {
        if (mode.equals("flee") || mode.equals("attack")) {
            return (int) Math.max(1, Math.round(speed * (energy / 100.0)));
        }
        return 1;
    }

    public void setTerritory(Set<Location> territory) {
        this.territory = territory;
    }

    public int getDamage() {
        return damage;
    }

    public boolean getIsDead() {
        return isDead;
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
        energy = maxEnergy;
    }

    public void wakeUp(World w) {
        isAwake = true;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(maxEnergy, energy));
    }

    public void actionCost(int satiation, int energy) {
        setSatiation(this.satiation - satiation);
        setEnergy(this.energy - energy);
    }

    private void setSatiation(int satiation) { this.satiation = Math.max(0, Math.min(MAX_SATIATION, satiation)); }

    private void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = Math.max(30, maxEnergy);
    }

    public void eat(World w, Edible edible) {
        int missingEnergy = maxEnergy - energy;
        int edibleNutrition = edible.getNutrition();
        setEnergy(energy + calcNutritionAbsorbed(edibleNutrition));
        edible.setNutrition(edibleNutrition - missingEnergy);
        if (edible.getNutrition() == 0) {
            edible.delete(w);
        }


    }

    public void delete(World w) {
        if (!isDead) { die(); }
        else if (w.getEntities().get(this) != null) {
            Location currL = w.getLocation(this);
            super.delete(w);
//            SET TILE CURRL MUSHROOM
            return;
        }
        super.delete(w);
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
        isAwake = false;
        energy = maxEnergy;
    }

    public int getHealth() {
        return health;
    }

    public boolean getIsMature() {
        return age >= MATURITY_AGE;
    }

    public int getEnergy() {
        return energy;
    }

    public void emerge(World w) {
        int radius = 3;
        Location l = HelperMethods.getClosestEmptyTile(w, w.getLocation(home), radius);
        isAwake = true;
        w.setCurrentLocation(l);
        w.setTile(l, this);
    }

    public void moveTo(World w, Location l) {
        if (mode.equals("flee")) {
            throw new IllegalStateException("Use flee() method for fleeing.");
        } else if (mode.equals("attack")) {
            throw new IllegalStateException("Use hunt() method for hunting.");
        }
        Location currL = w.getCurrentLocation();
        int distanceToL = HelperMethods.getDistance(currL, l);

        Set<Location> neighbours = w.getEmptySurroundingTiles();

        int minDistance = Integer.MAX_VALUE;
        Location bestMove = null;
        for (Location n : neighbours) {
            int nDistance = HelperMethods.getDistance(n, l);
            if (nDistance < minDistance) {
                minDistance = nDistance;
                bestMove = n;
            }
        }

        w.move(this, bestMove);
        actionCost(2, 2);
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
        System.out.println("WORKED");
        setHome(w, l, burrow);
        actionCost(2,10);
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
        actionCost(6, 9);
    }

    public Location getLocation(World w, Object object) {
        return w.getEntities().get(object);
    }

    private boolean isHorny(World w) {
        return energy > 70 && reproductionCooldown <= 0;
    }

    private void flee(World w, Location predatorLocation) {
        int currSpeed = getSpeed();
        Location targetL = null;
        List<Location> neighbours = new ArrayList<>(HelperMethods.getEmptySurroundingTiles(w, currSpeed));
        if (neighbours.isEmpty()) {
            throw new IllegalStateException("No empty tiles to flee to");
        } else if (home != null && home instanceof Hole) {
            Location homeL = w.getLocation(home);
            if (neighbours.contains(homeL)) {
                w.move(this, homeL);
                hide(w);
                return;
            }
        }
        neighbours.sort(Comparator.comparingInt(n -> HelperMethods.getDistance(n, predatorLocation)));
        w.move(this, neighbours.get(neighbours.size()-1));
        actionCost(2,4);

    }

    private void hunt(World w, Location preyLocation) {
        int currSpeed = Math.min(getSpeed(), HelperMethods.getDistance(w.getCurrentLocation(), preyLocation));
        List<Location> neighbours = new ArrayList<>(HelperMethods.getEmptySurroundingTiles(w, currSpeed));
        if (neighbours.isEmpty()) { throw new IllegalStateException("No empty tiles to flee to"); }
        neighbours.sort(Comparator.comparingInt(n -> HelperMethods.getDistance(n, preyLocation)));
        w.move(this, neighbours.get(0));
        actionCost(2,4);
    }

    private Edible findClosestEdible(World w) {
        Set<Location> edibleLocations = new HashSet<>();

        for (Location l : tilesInSight) {
            Object o = w.getTile(l);
            if (o instanceof Edible) {
                Edible edible = (Edible) o;
                if (edible.getNutrition() > 0) {
                    edibleLocations.add(l);
                }
            }
        }
        Location closestEdibleLocation = HelperMethods.findNearestLocationByTypes(w, w.getCurrentLocation(), edibleLocations, diet);
        return (closestEdibleLocation != null) ? (Edible) w.getTile(closestEdibleLocation) : null;
    }

    // Needs an override in wolf, to not detect animals in pack.
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
        System.out.println(predators);
        return predators;
    }

    private boolean isTired(World w) {
        return w.isNight() || energy < 20;
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
        Location partnerLocation = HelperMethods.findNearestLocationByType(w, w.getCurrentLocation(), partnerLocations, getClass().getSimpleName());
        System.out.println(this + " | " + w.getTile(partnerLocation));
        return (Animal) w.getTile(partnerLocation);
    }


    private String getMode(World w) {
        if (!isAwake) {
            return "stay";
        }
        boolean isPredatorInSight = isPredatorInSight(w);
        if (isPredatorInSight) {System.out.println("Predator " + findClosestPredator(w));}
        boolean isHungry = energy < 50;
        if (isHungry) {
            tilesInSight = w.getSurroundingTiles(20);
        }
        int activeAggression = (isHungry && aggression == 2) ? 3 : aggression;
        boolean isTired = isTired(w);
        Animal closestPartner = null;
        if (isHorny(w)) {
            closestPartner = findClosestPartner(w);
        }

        String mode = "flee";

        if (isPredatorInSight) {
            if (activeAggression == 3 || activeAggression == 2 && !isLargerPredatorInSight(w)) {
                mode = "attack";
            }
        } else if (w.getEmptySurroundingTiles().isEmpty()) {
            mode = "stay";
        } else if (home != null && isTired) {
            mode = "sleep";
        } else if (closestPartner != null) {
            mode = "reproduce";
        } else if (findClosestEdible(w) != null) {
            Edible edible = findClosestEdible(w);
            if (edible instanceof Animal && !((Animal) edible).isDead) {
                mode = "attack";
            } else { mode = "eat"; }
        } else {
            mode = "stay";
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
                Location predatorL = w.getEntities().get(findClosestPredator(w));
                flee(w, predatorL);
                return;
            }
            case "attack": {
                System.out.println("ATTACK");
                // find largest predator or non predator
                // if it is within 1 block radius, attack. else move to it
                Animal target = findLargestPredator(w);
                if (target == null) {
                    target = (Animal) findClosestEdible(w);
                }
                System.out.println(target);
                targetLocation = getLocation(w, target);
                if (w.getSurroundingTiles().contains(targetLocation)) {
                    attack(w, target);
                } else {
                    hunt(w, targetLocation);
                }
                return;
            }
            case "sleep": {
                System.out.println("SLEEP");
                targetLocation = getLocation(w, home);
                if (w.getCurrentLocation().equals(targetLocation)) {
                    sleep(w);
                    return;
                }
                break;
            }
            case "reproduce": {
                System.out.println("REPRODUCE");
                Animal partner = findClosestPartner(w);
                targetLocation = getLocation(w, partner);
                if (w.getSurroundingTiles().contains(targetLocation) && !w.getEmptySurroundingTiles().isEmpty()) {
                    reproduce(w, partner);
                    return;
                }
                break;
            }
            case "eat": {
                System.out.println("EAT");
                Edible edible = findClosestEdible(w);
                System.out.println(this + " " + edible);
                targetLocation = getLocation(w, edible);
                boolean isNonBlocking = edible instanceof NonBlocking;
                if (isNonBlocking && w.getCurrentLocation().equals(targetLocation)
                        || w.getSurroundingTiles().contains(targetLocation)) {
                    System.out.println(satiation);
                    eat(w, edible);
                    System.out.println(satiation);
                    return;
                }
                break;
            }
        }
        moveTo(w, targetLocation);
    }
}