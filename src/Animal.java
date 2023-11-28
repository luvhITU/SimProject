import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Set;

public abstract class Animal extends Edible implements Actor {

    private static final int MAX_ENERGY = 100;
    private static final int AGE_ENERGY_DECREASE = 5;
    private static final int ACTION_COST = 2;
    boolean isAwake;
    private double energy;
    private final Set<String> diet;
    private int stepAge;
    private int age;

    private final int damage;
    private final double absorptionPercentage; // Dictates percentage of nutrition absorbed and damage taken

    public Animal(Set<String> diet, int nutrition, int damage, double absorptionPercentage) {
        super(nutrition);
        this.diet = diet;
        energy = MAX_ENERGY;
        stepAge = 0;
        age = 0;
        this.damage = damage;
        this.absorptionPercentage = absorptionPercentage;
        this.isAwake = true;
    }

    public void act(World w) {
        if (getIsDead(w)) {return;}
        stepAge++;
        if (World.getTotalDayDuration() % stepAge == 0) {age();}
        actionCost();
    }

    @Override
    public boolean getIsDead(World w) {
        if (!super.getIsDead(w)) {
            if (energy == 0) {w.delete(this);}
        }
        return (super.getIsDead(w));
    }

    public double calcNutritionAbsorbed(Edible edible) {
        return Math.ceil(edible.getNutrition() * absorptionPercentage);
    }

    public int calcDamageTaken(Animal attacker) {
        return (int) Math.round(attacker.getDamage() * absorptionPercentage);
    }

    public void loseEnergy() {
        setEnergy(Math.max(0, energy - 2));
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

    public void awaken() {
        isAwake = true;
    }

    public double getEnergy() {
        return energy;
    }

    public void actionCost() {
        setEnergy(energy - ACTION_COST);
    }

    public void setEnergy(double energy) {
        this.energy = Math.max(0, Math.min(MAX_ENERGY, energy));
    }

    public void eat(World w, Edible edible) {
        w.delete(edible);
        setEnergy(energy + calcNutritionAbsorbed(edible));
        }

    private void age() {
        age++;
        setEnergy(energy - age * AGE_ENERGY_DECREASE);
    }
}




