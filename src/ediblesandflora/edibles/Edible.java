package ediblesandflora.edibles;

import itumulator.simulator.Actor;
import itumulator.world.World;

import static java.lang.Math.max;

public abstract class Edible implements Actor {

    protected int nutrition;
    protected final int maxNutrition;
    protected final int renewTimeDays;
    protected int stepAgeWhenDepleted;
    protected int stepAge;
    protected int age;

    /***
     * Constructs an Edible object
     * @param nutrition int
     * @param renewTimeDays number of days until the object renews itself after being depleted.
     *                      A value of 0 means the object is non-renewable
     */
    public Edible(int nutrition, int renewTimeDays) {
        this.nutrition = nutrition;
        maxNutrition = nutrition;
        this.renewTimeDays = renewTimeDays;
        stepAgeWhenDepleted = 0;
        stepAge = 0;
    }

    /***
     * See super
     * @param w providing details of the position on which the actor is currently located and much more.
     */
    @Override
    public void act(World w) {
        stepAge++;
        if (stepAge % World.getTotalDayDuration() == 0) { age(w); }

        if (renewTimeDays > 0 && isRenewableCooldownExpired()) {
            nutrition = maxNutrition;
        }
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
     * Returns true if nutrition is not 0 else returns false
     * @return  Boolean
     */
    public boolean isEdible() {
        return nutrition > 0;
    }

    /***
     * If renewable, starts countdown for renewing. If non-renewable, deletes the object.
     * @param w World
     */
    public void delete(World w) {
        if (renewTimeDays > 0) {
            stepAgeWhenDepleted = stepAge;
        } else {
            w.delete(this);
        }

    }

    private boolean isRenewableCooldownExpired() {
        return stepAge - stepAgeWhenDepleted >= renewTimeDays * World.getTotalDayDuration();
    }

    /***
     * Uses age++
     * @param w World
     */
    public void age(World w) {
        age++;
    }
}
