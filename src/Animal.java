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

    protected final int maxHealth;
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
        }
        else {
            Set<Location> tilesInSight = w.getSurroundingTiles(5);
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
            imageKeyBuilder.append("sleeping");
        }
        return new DisplayInformation(Color.magenta, imageKeyBuilder.toString());
    }

    public boolean isDead() {
        return satiation == 0 || health == 0;
    }

    private void deleteIfDead(World w) {
        if (isDead()) { delete(w); }
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
        animal.wakeUp(w);
        animal.health -= damage;
        if (animal.health <= 0) {
            animal.delete(w);
        }
    }

    public int calcMaxSpeed() {
        return (int) Math.max(1, Math.round(maxSpeed * (energy / 100.0)));
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

    protected void setSatiation(int satiation) {
        this.satiation = Math.max(0, Math.min(MAX_SATIATION, satiation));
    }

    private void setMaxEnergy(int maxEnergy) {
        int minMaxEnergy = 30;
        this.maxEnergy = Math.max(minMaxEnergy, maxEnergy);
    }

    public void eat(World w, Edible edible) {
        int missingSatiation = MAX_SATIATION - satiation;
        int edibleNutrition = edible.getNutrition();
        setSatiation(satiation + calcNutritionAbsorbed(edibleNutrition));
        edible.setNutrition(edibleNutrition - missingSatiation);
        if (edible.getNutrition() <= 0) {
            edible.delete(w);
        }
    }

    public void delete(World w) {
        //Todo become carcass
        w.delete(this);
    }

    private void age() {
        age++;
        setMaxEnergy(BASE_MAX_ENERGY - age * AGE_MAX_ENERGY_DECREASE);
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

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(maxEnergy, energy));
    }

    public void emerge(World w) {
        int radius = 3;
        Location l = HelperMethods.getClosestEmptyTile(w, w.getLocation(home), radius);
        isAwake = true;
        w.setCurrentLocation(l);
        w.setTile(l, this);
    }

    public void moveTo(World w, Location l) {
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
        actionCost(2);
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
        actionCost(2);
    }

//    public void reproduce(World w, Animal partner) {
//        Location l = HelperMethods.getClosestEmptyTile(w, 1);
//        Animal lilBaby = null;
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

    public Location getLocation(World w, Object object) {
        return w.getEntities().get(object);
    }

    private boolean canMate(World w) {
        boolean matingCooldownExpired = stepAge - stepAgeWhenMated >= matingCooldownDays * World.getTotalDayDuration();
        return isBreedable && energy > 70 && matingCooldownExpired;
    }

    private void flee(World w, Location predatorLocation) {
        int currSpeed = calcMaxSpeed();
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
        w.move(this, neighbours.get(neighbours.size() - 1));
        actionCost(2);

    }

    protected void hunt(World w) {
        Object target = findClosestEdible(w);
        Location targetLoc = w.getLocation(target);
        boolean targetInRange = false;
        if (target instanceof NonBlocking && w.getCurrentLocation().equals(targetLoc)) {
            targetInRange = true;
        } else if (w.getSurroundingTiles().contains(targetLoc)) {
            targetInRange = true;
        }

        if (target instanceof Edible) {
            Edible edible = (Edible) target;
            if (targetInRange) {
                eat(w, edible);
            } else {
                moveTo(w, targetLoc);
                actionCost(2);
            }
            return;
        }
        Animal prey = (Animal) target;
        if (targetInRange) {
            attack(w, prey);
        } else {
            int currSpeed = calcMaxSpeed();
            List<Location> neighbours = new ArrayList<>(HelperMethods.getEmptySurroundingTiles(w, currSpeed));
            neighbours.sort(Comparator.comparingInt(n -> HelperMethods.getDistance(n, targetLoc)));
            w.move(this, neighbours.get(0));
            actionCost(2);
        }


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
        return HelperMethods.findNearestOfObjects(w, findEdibles(w));
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
            if (animal.diet.contains(thisType)) {
                predators.add(animal);
            }
        }
        System.out.println(predators);
        return predators;
    }

    private boolean isTired(World w) {
        return w.isNight() || energy < 20;
    }

    public void randomMove(World w) {
        Set<Location> neighbours = w.getEmptySurroundingTiles();
        Location l = (Location) neighbours.toArray()[HelperMethods.getRandom().nextInt(neighbours.size())];
        w.move(this, l);
        actionCost(2);
    }

    private Animal findClosestPartner(World w) {
        Set<Location> partnerLocations = new HashSet<>();
        for (Location n : tilesInSight) {
            Object entity = w.getTile(n);
            if (entity == null || !getClass().equals(entity.getClass())) {
                continue;
            }
            Animal potentialPartner = (Animal) entity;
            if (potentialPartner.canMate(w)) {
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


//    private String getMode(World w) {
//        if (!isAwake) {
//            return "stay";
//        }
//        boolean isPredatorInSight = isPredatorInSight(w);
//        if (isPredatorInSight) {
//            System.out.println("Predator " + findClosestPredator(w));
//        }
//        boolean isHungry = satiation < 50;
//        int activeAggression = (isHungry && aggression == 2) ? 3 : aggression;
//        boolean isTired = isTired(w);
//        Animal closestPartner = null;
//        boolean hasLegalMove = !w.getEmptySurroundingTiles().isEmpty();
//        if (canMate(w)) {
//            closestPartner = findClosestPartner(w);
//        }
//
//        String mode = "flee";
//
//        if (isPredatorInSight) {
//            if (activeAggression == 3 || activeAggression == 2 && !isLargerPredatorInSight(w)) {
//                mode = "attack";
//            }
//        } else if (!hasLegalMove) {
//            mode = "stay";
//        } else if (home != null && isTired) {
//            mode = "sleep";
//        } else if (closestPartner != null) {
//            mode = "reproduce";
//        } else if (satiation < 80 && findClosestEdible(w) != null) {
//            Edible edible = findClosestEdible(w);
//            if (edible instanceof Animal && !((Animal) edible).isDead) {
//                mode = "attack";
//            } else {
//                mode = "eat";
//            }
//        } else {
//            mode = "random move";
//        }
//        return mode;
//    }

//    private void aiPackage(World w) {
//        String mode = getMode(w);
//        Location targetLocation = null;
//        switch (mode) {
//            case "stay": {
//                System.out.println("STAY");
//                return;
//            }
//            case "flee": {
//                System.out.println("FLEE");
//                Location predatorL = w.getEntities().get(findClosestPredator(w));
//                flee(w, predatorL);
//                return;
//            }
//            // TODO Man skal kunne flytte sig mod prey og attacke i samme step.
//            case "attack": {
//                System.out.println("ATTACK");
//                // find largest predator or non predator
//                // if it is within 1 block radius, attack. else move to it
//                Animal target = findLargestPredator(w);
//                if (target == null) {
//                    target = (Animal) findClosestEdible(w);
//                }
//                System.out.println(target);
//                targetLocation = getLocation(w, target);
//                if (w.getSurroundingTiles().contains(targetLocation)) {
//                    attack(w, target);
//                } else {
//                    hunt(w, targetLocation);
//                }
//                return;
//            }
//            case "sleep": {
//                System.out.println("SLEEP");
//                targetLocation = getLocation(w, home);
//                if (w.getCurrentLocation().equals(targetLocation)) {
//                    sleep(w);
//                    return;
//                }
//                break;
//            }
//            case "reproduce": {
//                System.out.println("REPRODUCE");
//                Animal partner = findClosestPartner(w);
//                targetLocation = getLocation(w, partner);
//                if (w.getSurroundingTiles().contains(targetLocation) && !w.getEmptySurroundingTiles().isEmpty()) {
//                    reproduce(w, partner);
//                    return;
//                }
//                break;
//            }
//            case "eat": {
//                System.out.println("EAT");
//                Edible edible = findClosestEdible(w);
//                System.out.println(this + " " + edible);
//                targetLocation = getLocation(w, edible);
//                boolean isNonBlocking = edible instanceof NonBlocking;
//                if (isNonBlocking && w.getCurrentLocation().equals(targetLocation)
//                        || w.getSurroundingTiles().contains(targetLocation)) {
//                    System.out.println(satiation);
//                    eat(w, edible);
//                    System.out.println(satiation);
//                    return;
//                }
//                break;
//            }
//
//            case "random move": {
//                randomMove(w);
//                return;
//            }
//        }
//        moveTo(w, targetLocation);
//    }
}