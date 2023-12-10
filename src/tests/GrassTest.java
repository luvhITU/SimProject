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
    public void testGetInformationReturnsDisplayInformation() {
        Grass grass = new Grass();
        assertNotNull(grass.getInformation());
    }

    @Test
    public void reproduceCheckTest() {
        Grass grass = new Grass();
        w.setTile(new Location(0, 0), grass);
        Map<Object, Location> entities = null;

        for (int i = 0; i < 20; i++) {
            p.simulate();
            entities = w.getEntities();
        }
        assertTrue(entities.size() > 1);
    }

    @Test
    public void deleteCheckTest() {
        Grass grass = new Grass();
        w.setTile(new Location(0, 0), grass);
        for (int i = 0; i < 200; i++) {
            p.simulate();
        }
        assertFalse(w.contains(grass));
    }
}