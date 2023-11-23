import itumulator.executable.Program;
import itumulator.simulator.Actor;
import itumulator.world.World;

import java.util.*;

abstract class Animal extends SimComponent implements Actor, Perishable {


    private double STEP_SATIATION_DECRASE = 0.25;

    private int MAX_SATIATION = 10;
    private double satiation;
    private HashSet<String> foodSources;
    private int stepOfBirth;
    private double expirationMultiplier;
    private double age;

    public Animal(Program p, HashSet<String> foodSources, double expirationMultiplier) {
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
    }

    public void act(World w) {
        System.out.println(satiation);
        if (w.getCurrentTime() == 0 && s.getSteps() > stepOfBirth) {age++;}
        expirationCheck();

        satiation -= STEP_SATIATION_DECRASE;
        if (satiation <= 0) {w.delete(this);}
    }

    public void expirationCheck() {
        int initialUpperBound = 200;
        int calculatedUpperBound = (int) (initialUpperBound + age * expirationMultiplier);
        double chance = new Random().nextInt(calculatedUpperBound);
        if (chance > initialUpperBound) {
            w.delete(this);
        }
    }

    public HashSet<String> getFoodSources() {
        return foodSources;
    }

    public int getMaxSatiation() {
        return MAX_SATIATION;
    }

    public double getSatiation() {
        return satiation;
    }

    public void setSatiation(double satiation) {
        this.satiation = satiation;
    }
}

