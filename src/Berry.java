import com.sun.jdi.event.StepEvent;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;

public class Berry extends Edible implements Actor, DynamicDisplayInformationProvider {

    private int stepAge;
    private  int eatenAge;
    private int regrowTime;


    public Berry() {
        super(Config.BerryBush.NUTRITION);
        stepAge = 0;
        regrowTime = 30;
    }

    @Override
    public void act(World world) {
        stepAge++;
        if (stepAge - eatenAge >= regrowTime) {
            setNutrition(Config.BerryBush.NUTRITION);
        }
    }

    @Override
    public DisplayInformation getInformation() {
            if (getNutrition() > 0) {
            return new DisplayInformation(Color.red, "bush-berries");
        } else {
            return new DisplayInformation(Color.green, "bush");
        }
    }

    @Override
    public void delete(World w) { eatenAge = stepAge; }
}
