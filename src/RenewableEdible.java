import itumulator.world.World;

public abstract class RenewableEdible extends Edible {
    private int renewTimeDays;
    private final int maxNutrition;
    private int stepAgeWhenDepleted;
    public RenewableEdible(int nutrition, int renewTimeDays) {
        super(nutrition);
        maxNutrition = nutrition;
        this.renewTimeDays = renewTimeDays;
    }

    @Override
    public void act(World w) {
        if (getStepAge() - stepAgeWhenDepleted >= renewTimeDays * World.getTotalDayDuration()) {
            setNutrition(maxNutrition);
        }
    }

    @Override
    public void delete(World w) { stepAgeWhenDepleted = getStepAge(); }


}
