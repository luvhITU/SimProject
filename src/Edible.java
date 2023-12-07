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

    public void delete(World w) {
        w.delete(this);
    }

    public int getNutrition() {
        return nutrition;
    }


    public void reduceNutritionBy(int reduceBy) { this.nutrition -= max(0, reduceBy); }

    protected void setNutrition(int nutrition) {
        this.nutrition = nutrition;
    }

    protected int getStepAge() {
        return stepAge;
    }

    protected int getAge() {
        return age;
    }

    public boolean isEdible() {
        return nutrition != 0;
    }
}
