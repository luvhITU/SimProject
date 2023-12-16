package tests;

import animals.Rabbit;
import animals.packanimals.Wolf;
import ediblesandflora.edibles.Carcass;
import itumulator.world.Location;
import org.junit.Assert;
import org.junit.Test;

public class WolfTest extends test{
    Wolf wolf1 = new Wolf();
    Wolf wolf2 = new Wolf();

    @Test
    public void burrowReproductionTest() { doesBurrowReproduceCorrectly(wolf1, wolf2); };
    @Test
    public void canHuntTest(){
        Rabbit r = new Rabbit();
        Carcass c = null;
        w.setTile(startLocation,wolf1);
        w.setTile(new Location(0,1),r);
        for(int i = 0;w.contains(r) && i < 100;i++){
            wolf1.attack(w,r);
        }
        for(Object o: w.getEntities().keySet()){
            if(o instanceof Carcass){
                c = (Carcass) o;
                break;
            }
        }
        if(c == null){
            Assert.fail(); //needs to fail if there is no carcass
        }
        for(int i = 0;w.getEntities().containsKey(c) && i < 100; i++){
            wolf1.eat(w,c);
        }
        System.out.println(w.getEntities().keySet());
    }

}
