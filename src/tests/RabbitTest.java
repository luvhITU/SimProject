package tests;

import animals.Animal;
import ediblesandflora.edibles.Grass;
import animals.Rabbit;
import homes.*;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class RabbitTest extends test {
    protected Rabbit r = new Rabbit();

    @Test
    public void hasMovedTest(){
        hasMoved(r);
    }
    @Test
    public void losesEnergyTest(){
        losesEnergy(r);
    }
    @Test
    public void losesSatiationTest(){
        losesSatiation(r);
    }
    @Test //k1-2b. Kaniner kan dø, hvilket resulterer I at de fjernes fra verdenen.
    public void deleteTest(){
        delete(r);
    }
    @Test //K1-2c "Kaniner lever af græs som de spiser i løbet af dagen,"
    public void hasEatenTest(){
        Grass g = new Grass();
        Home h = new Home(new Location(5,5),5,"Rabbit");
        w.setTile(startLocation,g);
        w.setTile(startLocation,r);
        r.setHome(w,h); //Sets home before to not get exception
        Object[] beforeEntities = w.getEntities().keySet().toArray();
        System.out.println(Arrays.toString(beforeEntities));
        int i = 0;
        while(g.getNutrition() > 0 || i > 20) {
            r.eat(w, g);
            i++;
        }
        Object[] afterEntities = w.getEntities().keySet().toArray();
        System.out.println(Arrays.toString(afterEntities));
        Assert.assertFalse(Arrays.equals(beforeEntities,afterEntities));
    }
    @Test //K1-2c "uden mad dør en kanin."
    public void dieWithTimeTest(){
        dieWithTime(r);
    }
    @Test
    public void hasDugBurrowTest(){
        w.setTile(startLocation,r);
        r.act(w);
        Home h = null;
        for(Object o: w.getEntities().keySet()){
            if(o instanceof Burrow){
                h = (Home) o;
                System.out.println("getAllowedSpecies():" + h.getAllowedSpecies());
            }
        }
        Assert.assertNotNull(h);
    }

    //Mating does not work right now @Test
    public void hasMated(){
        Rabbit r2 = new Rabbit();
        Location startLocation2 = new Location(0,1);
        w.setTile(startLocation,r);
        w.setTile(startLocation2,r2);
    }
}