package tests;

import ediblesandflora.edibles.Grass;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.Map;
import org.junit.*;
import static org.junit.Assert.*;

public class GrassTest extends test {

    @Test
    public void reproduceCheckTest() {
        Grass grass = new Grass();
        w.setTile(new Location(0, 0), grass);
        Map<Object, Location> entities = null;

        for (int i = 0; i < 20; i++) {
            grass.act(w);
            w.step();
            entities = w.getEntities();
        }
        assertTrue(entities.size() > 1);
    }
}