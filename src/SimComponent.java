import itumulator.world.World;

import java.awt.*;

public abstract class SimComponent {

    public boolean getIsDead(World w) {
        return !w.getEntities().containsKey(this);
    }

    public void delete(World w) {
        try {
            w.delete(this);
        } catch(IllegalArgumentException ignore) {}

    }
}








