package Abstracts;

import Abstracts.Edible;
import itumulator.world.World;

public abstract class RenewableEdible extends Edible {
    private int renewTimeDays;
    private final int maxNutrition;
    private int stepAgeWhenDepleted;

    /***
     * Sets maxNutrition to input nutrition int and sets renewTimeDays
     * @param nutrition         int
     * @param renewTimeDays     int
     */
    public RenewableEdible(int nutrition, int renewTimeDays) {
        super(nutrition);
        maxNutrition = nutrition;
        this.renewTimeDays = renewTimeDays;
    }

    /***
     * See super
     * @param w providing details of the position on which the actor is currently located and much more.
     */
    @Override
    public void act(World w) {
        if (getStepAge() - stepAgeWhenDepleted >= renewTimeDays * World.getTotalDayDuration()) {
            setNutrition(maxNutrition);
        }
    }

    /***
     * Changes nutrition value
     * @param w World
     */
    @Override
    public void delete(World w) { stepAgeWhenDepleted = getStepAge(); }


}
