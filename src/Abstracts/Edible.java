package Abstracts;

import Abstracts.SimComponent;
import itumulator.simulator.Actor;
import itumulator.world.World;

import static java.lang.Math.max;

public abstract class Edible extends SimComponent implements Actor {
    private int nutrition;
    private int stepAge;
    private int age;

    public Edible(int nutrition) {
        this.nutrition = nutrition;
        stepAge = 0;
    }

    @Override
    public void act(World w) {
        stepAge++;
        if (stepAge % World.getTotalDayDuration() == 0) { age++; }
    }

    /***
     * Deletes this object
     * @param w World
     */
    public void delete(World w) {
        w.delete(this);
    }

    /***
     * Getter for Nutrition
     * @return  int
     */
    public int getNutrition() {
        return nutrition;
    }

    /***
     * Reduces nutrition by input int
     * @param reduceBy  int
     */
    public void reduceNutritionBy(int reduceBy) { this.nutrition -= max(0, reduceBy); }

    /***
     * Sets nutrition by input int
     * @param nutrition int
     */
    public void setNutrition(int nutrition) {
        this.nutrition = nutrition;
    }
    protected int getStepAge() {
        return stepAge;
    }

    protected int getAge() {
        return age;
    }

    /***
     * Returns true if nutrition is not 0 else returns false
     * @return  Boolean
     */
    public boolean isEdible() {
        return nutrition != 0;
    }
}
