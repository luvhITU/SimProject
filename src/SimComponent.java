import itumulator.world.World;

import java.awt.*;

public abstract class SimComponent {

    public void delete(World w) {
        try {
            w.delete(this);
        } catch(IllegalArgumentException ignore) {}

    }
}








