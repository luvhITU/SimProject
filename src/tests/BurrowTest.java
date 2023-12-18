package tests;

import animals.Rabbit;
import ediblesandflora.edibles.Grass;
import homes.Burrow;
import org.junit.Test;
import utils.Config;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class BurrowTest extends test{
    private Burrow rabbitBurrow = new Burrow(startLocation, Config.Rabbit.MAX_BURROW_OCCUPANTS,"Rabbit");

    //K1-3b. Dyr kan stå på et kaninhul uden der sker noget
    @Test
    public void nonBlockingPositive(){
        Rabbit r = new Rabbit();
        w.setTile(startLocation,rabbitBurrow);
        assertDoesNotThrow( () ->
                w.setTile(startLocation,r)
        );
    }
    @Test
    public void nonBlockingNegative(){
        w.setTile(startLocation,rabbitBurrow);
        assertThrows( IllegalArgumentException.class, () ->
                w.setTile(startLocation,new Grass())
        );
    }
}
