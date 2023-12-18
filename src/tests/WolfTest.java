package tests;

import animals.Rabbit;
import animals.packanimals.Wolf;
import ediblesandflora.edibles.Carcass;
import ediblesandflora.edibles.Grass;
import itumulator.world.Location;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertThrows;

public class WolfTest extends test{
    Wolf wolf1 = new Wolf();
    Wolf wolf2 = new Wolf();

    @Test
    public void burrowReproductionTest() { doesBurrowReproduceCorrectly(wolf1, wolf2); };
    @Test
    public void canHuntTest(){
        Rabbit r = new Rabbit();
        Carcass c;
        w.setTile(startLocation,wolf1);
        w.setTile(new Location(0,1),r);
        System.out.println(w.getEntities().keySet());
        for(int i = 0; w.contains(r) && i < 100; i++){
            wolf1.attack(w,r);
        }
        System.out.println(w.getEntities().keySet());
        c = (Carcass) w.getEntities().keySet().stream().filter(o -> o instanceof Carcass).findFirst().orElse(null);
        if(c == null){
            Assert.fail(); //needs to fail if there is no carcass
        }
        for(int i = 0;w.getEntities().containsKey(c) && i < 100; i++){
            wolf1.eat(w,c);
        }
        System.out.println(w.getEntities().keySet());
        assertThrows( IllegalArgumentException.class, () ->
                w.getLocation(c)
        );
    }

}
