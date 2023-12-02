import com.sun.jdi.event.StepEvent;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.awt.*;

public class Berry extends Edible implements Actor, NonBlocking, DynamicDisplayInformationProvider {

    private int stepAge;
    private  int eatenAge;
    private int regrowTime;
    private boolean hasBerries;

    public Berry() {
        super(Config.BerryBush.NUTRITION);
        stepAge = 0;
        regrowTime = 30;
        hasBerries = true;
    }

    @Override
    public void act(World world) {
        stepAge++;
        if (!hasBerries) {
            regrowBerries();
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if (hasBerries) {
            return new DisplayInformation(Color.red, "bush-berries");
        } else {
            return new DisplayInformation(Color.green, "bush");
        }
    }

    public boolean getHasBerries() {
        return hasBerries;
    }

    public void eatBerries() { //USE THIS WHEN EATING BERRIES
        hasBerries = false;
        eatenAge = stepAge;
    }

    public void regrowBerries() {
        if ((stepAge - eatenAge) >= regrowTime) {
            hasBerries = true;
        }
    }
}
