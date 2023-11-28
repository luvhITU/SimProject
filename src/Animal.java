import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.util.Set;

abstract class Animal extends Edible implements Actor {


    private static final int STEP_SATIATION_DECREASE = 1;
    private static final int MAX_SATIATION = 100;

    private static final int MAX_ENERGY = 100;
    private static final int AGE_ENERGY_DECREASE = 5;
    boolean isAwake;
    private double satiation;
    private double energy;
    private final Set<String> diet;
    private int stepAge;
    private int age;

    private final int damage;
    private final double absorptionPercentage; // Dictates percentage of nutrition absorbed and damage taken

    public Animal(Set<String> diet, int nutrition, int damage, double absorptionPercentage) {
        super(nutrition);
        this.diet = diet;
        satiation = MAX_SATIATION;
        energy = MAX_ENERGY;
        stepAge = 0;
        age = 0;
        this.damage = damage;
        this.absorptionPercentage = absorptionPercentage;
        this.isAwake = true;
    }

    public void act(World w) {
        stepAge++;
        if (World.getTotalDayDuration() % stepAge == 0) {
            age();
        }
        satiation -= STEP_SATIATION_DECREASE;
        if (satiation <= 0) { // Todo - måske de er "cleaner" at sikre os at satiation aldrin er mindre end 0;
            getWorld().delete(this);
        }
    }

    public double calcNutritionAbsorbed(Edible edible) {
        return Math.ceil(edible.getNutrition() * absorptionPercentage);
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

    public double getSatiation() {
        return satiation;
    }

    public void setSatiation(double satiation) {
        this.satiation = Math.min(satiation, MAX_SATIATION);
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

    public void setEnergy(double energy) {
        this.energy = Math.max(0, Math.min(MAX_ENERGY, energy));
    }

    public void tryToMove(Location l) {
        double moveProbability = energy / MAX_ENERGY;
        if (getRandom().nextDouble() < moveProbability) {
            getWorld().move(this, l);
        }
    }

    public void eat(Edible edible) {
        try {
            getWorld().delete(edible);
            setSatiation(getSatiation() + edible.getNutrition());
        } catch (IllegalArgumentException e) {
            System.out.println(edible + " was deleted by another process before it was eaten");
            return;
        }
    }

    private void age() {
        age++;
        setEnergy(energy - age * AGE_ENERGY_DECREASE);
    }
}




