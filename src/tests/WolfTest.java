package tests;

import animals.Rabbit;
import animals.packanimals.Wolf;
import ediblesandflora.edibles.Carcass;
import itumulator.world.Location;
import org.junit.Assert;
import org.junit.Test;

public class WolfTest extends test{
    Wolf a = new Wolf();
    Wolf b = new Wolf();

    @Test
    public void burrowReproductionTest() { doesBurrowReproduceCorrectly(a, b); };
    @Test
    public void hasMovedTest(){
        hasMoved(a);
    }
    @Test
    public void losesEnergyTest(){
        losesEnergy(a);
    }
    @Test
    public void losesSatiationTest(){ losesSatiation(a); }
    @Test
    public void deleteTest(){
        delete(a);
    }
    @Test
    public void canHuntTest(){
        Rabbit r = new Rabbit();
        Carcass c = null;
        w.setTile(startLocation,a);
        w.setTile(new Location(0,1),r);
        for(int i = 0;w.contains(r) && i < 100;i++){
            a.attack(w,r);
        }
        for(Object o: w.getEntities().keySet()){
            if(o instanceof Carcass){
                c = (Carcass) o;
                break;
            }
        }
        if(c == null){
            Assert.assertTrue(false); //needs to fail if there is no carcass
        }
        for(int i = 0;w.getEntities().containsKey(c) && i < 100; i++){
            a.eat(w,c);
        }
        System.out.println(w.getEntities().keySet());
    }

}
