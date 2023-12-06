import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.Map;
import org.junit.*;
import static org.junit.Assert.*;

public class GrassTest {

    @Test
    public void testGetInformationReturnsDisplayInformation() {
        Grass grass = new Grass();
        assertNotNull(grass.getInformation());
    }

    @Test
    public void testTryReproduceCreatesNewGrass() {
        int size = 5;
        int delay = 10;
        int display_size = 800;

        Program p = new Program(size, display_size, delay);
        World w = p.getWorld();

        Grass grass = new Grass();
        w.setTile(new Location(0, 0), grass);
        Map<Object, Location> entities = null;

        p.show();
        for (int i = 0; i < 20; i++) {
            p.simulate();
            entities = w.getEntities();
        }
        assertTrue(entities.size() > 1);
    }

    @Test
    public void testIsDeadAfterExpirationCheck() {
        int size = 5;
        int delay = 10;
        int display_size = 800;

        Program p = new Program(size, display_size, delay);
        World w = p.getWorld();

        Grass grass = new Grass();
        w.setTile(new Location(0, 0), grass);

        p.show();
        for (int i = 0; i < 20; i++) {
            p.simulate();
        }
        assertTrue(grass.getIsDead(w));
    }
}
