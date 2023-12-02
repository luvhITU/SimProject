import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;

public abstract class Animal extends Edible implements Actor, DynamicDisplayInformationProvider {


    private static final int MATURITY_AGE = 3;
    private static final int BASE_MAX_ENERGY = 100;
    private static final int AGE_ENERGY_DECREASE = 5;
    private static final int ACTION_COST = 2;
    private final Set<String> diet;
    private final int damage;
    private final double absorptionPercentage; // Dictates percentage of nutrition absorbed and damage taken
    private boolean isAwake;
    private Home home;
    private int energy;
    private int maxEnergy;
    private int stepAge;
    private int age;

    private boolean hasMatedToday;

    public Animal(Set<String> diet, int nutrition, int damage, double absorptionPercentage) {
        super(nutrition);
        this.diet = diet;
        energy = BASE_MAX_ENERGY;
        maxEnergy = BASE_MAX_ENERGY;
        stepAge = 0;
        age = 0;
        this.damage = damage;
        this.absorptionPercentage = absorptionPercentage;
        this.isAwake = true;
        home = null;
        hasMatedToday = false;
    }

    public void act(World w) {
        if (getIsDead(w)) {
            return;
        }
        stepAge++;
        if (stepAge % World.getTotalDayDuration() == 0) {
            age();
        }
        if (w.getEntities().get(this) != null) { actionCost(); }
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

    @Override
    public boolean getIsDead(World w) {
        if (energy == 0) {
            delete(w);
        }
        return (super.getIsDead(w));
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
        return (int) Math.round(edible.getNutrition() * absorptionPercentage);
    }

    public int calcDamageTaken(Animal attacker) {
        return (int) Math.round(attacker.getDamage() * absorptionPercentage);
    }


    public int getDamage() {
        return damage;
    }

    public Set<String> getDiet() {
        return diet;
    }

    public boolean getIsAwake() {
        return isAwake;
    }

    public void sleep() {
        isAwake = false;
    }

    public void wakeUp() {
        isAwake = true;
    }

    public void setEnergy(int energy) {
        this.energy = Math.max(0, Math.min(BASE_MAX_ENERGY, energy));
    }

    public int getEnergy() {
        return energy;
    }

    public void actionCost() {
        setEnergy(energy - ACTION_COST);
    }

    private void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = Math.max(30, maxEnergy);
    }

    public void eat(World w, Edible edible) {
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
        sleep();
    }

    public boolean getIsMature() {
        return age >= MATURITY_AGE;
    }

    public boolean getHasMatedToday() { return hasMatedToday; }

    public void emerge(World w) {
        int radius = 3;
        Location l = HelperMethods.getClosestEmptyTile(w, w.getLocation(home), radius);
        wakeUp();
        w.setCurrentLocation(w.getLocation((home)));
        w.setTile(l, this);
    }

    /***
     * Moves to a nearby tile in the direction of input Location l
     * @param World w
     * @param Location l
     */
    public void moveToLocation(World w,Location l) {
        Set<Location> neighbours = w.getEmptySurroundingTiles(w.getLocation(this));
        if (neighbours.isEmpty()) {throw new IllegalStateException("No empty tiles to move to");}

        try {
            Location currL = w.getLocation(this);
            if (currL.equals(l)) {
                return;
            }
            int minDistance = Integer.MAX_VALUE;
            Location bestMove = null;
            for (Location n : neighbours) {
                int nDistance = Math.abs(n.getX() - l.getX()) + Math.abs((n.getY() - l.getY()));
                if (nDistance < minDistance) {
                    minDistance = nDistance;
                    bestMove = n;
                }
            }
            if (bestMove != null) {
                w.move(this, bestMove);
            }
        } catch (IllegalArgumentException iae) {
            //System.out.println(iae.getMessage());
        }
    }

    public void findHome(World w, String type) {
        ArrayList<Home> availableBurrows = HelperMethods.availableHomes(w, type);
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

    /***
     * I am a bit tired but there should probably be some text here to explain what it does
     * @param w
     */
    public void tryToMate(World w) {
        if (!getIsMature()) { return; }
        boolean foundPartner = false;
        Set<Location> neighbours = w.getSurroundingTiles();
        for (Location n : neighbours) {
            Object entity = w.getTile(n);
            if (entity != null && this.getClass() == entity.getClass()) {
                Animal partner = (Animal) entity;
                if(partner.getIsMature()) {
                    foundPartner = true;
                    hasMatedToday = true;
                    partner.hasMatedToday = true;
                    break;
                }
            }
        }
        if (!foundPartner) { return; }


        int radiusToPlaceChild = 3;
        Location l = HelperMethods.getClosestEmptyTile(w, w.getCurrentLocation(), radiusToPlaceChild);
        Animal lilBaby = null;
        try {
            lilBaby = this.getClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException ignore) {
        }
        w.setTile(l, lilBaby);
        actionCost();
    }
}