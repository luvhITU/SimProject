import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.World;

import java.util.*;

abstract class Animal extends SimComponent implements Actor, Perishable {


    private double STEP_SATIATION_DECRASE = 0.25;

    private int MAX_SATIATION = 10;
    private double satiation;
    private Set<String> foodSources;
    private int stepOfBirth;
    private double expirationMultiplier;
    private double age;

    boolean isAwake;

    public Animal(Program p, Set<String> foodSources, double expirationMultiplier) {
        super(p);
        this.foodSources = foodSources;
        satiation = MAX_SATIATION;
        /*
        stepAge is used for dayAge calculation. Since dayAges should be updated at the start of each new day,
        stepAge starts current time of day.
         */
        stepOfBirth = s.getSteps();
        age = 0;
        this.expirationMultiplier = expirationMultiplier;
        this.isAwake = true;
    }

    public void act(World w) {
        System.out.println(this + " satiation: " + satiation);
        if (w.getCurrentTime() == 0 && s.getSteps() > stepOfBirth) {age++;}
        satiation -= STEP_SATIATION_DECRASE;
        if (satiation <= 0) {die();}
        expirationCheck();
    }

    public void expirationCheck() {
        int initialUpperBound = 200;
        int calculatedUpperBound = (int) (initialUpperBound + age * expirationMultiplier);
        double chance = new Random().nextInt(calculatedUpperBound);
        if (chance > initialUpperBound) {
            die();
        }
    }

    public Set<String> getFoodSources() {
        return foodSources;
    }

    public int getMaxSatiation() {
        return MAX_SATIATION;
    }

    public double getSatiation() {
        return satiation;
    }

    public void setSatiation(double satiation) {
        this.satiation = Math.min(satiation, MAX_SATIATION);
    }

    public void eat(Edible edible) {
        try {
            w.delete(edible);
            setSatiation(getSatiation() + edible.getNutrition());
        } catch (IllegalArgumentException e) {
            System.out.println(edible +  " was deleted by another process before it was eaten");
            return;
        }
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
}


