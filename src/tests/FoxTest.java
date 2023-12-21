package tests;

import animals.packanimals.Fox;
import itumulator.world.Location;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class FoxTest extends test {
    Fox f = new Fox();
    @Test
    public void movesAtNight(){
        //Test that it doesn't move during the day
        w.setTile(startLocation,f);
        while(w.getCurrentTime() <= 10){
            w.step();
        }
        System.out.println(w.getLocation(f));
        Location l = w.getLocation(f);
        Assertions.assertEquals(startLocation,l);
        //Test that it moves at night
        w.setNight();
        f.act(w);
        Assertions.assertNotEquals(l,w.getLocation(f));
    }

}
