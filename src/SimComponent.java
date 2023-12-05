import itumulator.world.World;

import java.awt.*;

public abstract class SimComponent {
    public String getType() {
        return getClass().getSimpleName();
    }

    public abstract void delete(World w);
}








