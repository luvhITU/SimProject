package tests;

import animals.Rabbit;
import ediblesandflora.edibles.Grass;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.util.Map;
import org.junit.*;
import org.junit.jupiter.api.Assertions;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GrassTest extends test {
    Grass grass = new Grass();

    //K1-1c Græs kan sprede sig.
    @Test
    public void spreadTest() {

        w.setTile(startLocation, grass);
        Map<Object, Location> entities = null;

        for (int i = 0; i < 100 && w.getEntities().size() < 2; i++) {
            grass.act(w);
            entities = w.getEntities();
            System.out.println(entities.size());
        }
        assert entities != null;
        assertTrue(entities.size() > 1);
    }
    //k1-1d. Dyr kan stå på græs uden der sker noget med græsset (Her kan interfacet
    //NonBlocking udnyttes).
    @Test
    public void nonBlockingPositive(){
        w.setTile(startLocation,grass);
        assertDoesNotThrow( () ->
                 w.setTile(startLocation,new Rabbit())
        );
    }
    //Make sure it throws if there are places 2 non-blocking on the same tile
    @Test
    public void nonBlockingNegative(){
        w.setTile(startLocation,grass);
        assertThrows( IllegalArgumentException.class, () ->
                w.setTile(startLocation,new Grass())
        );
    }
}