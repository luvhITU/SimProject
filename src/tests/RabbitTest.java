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
    protected Rabbit a = new Rabbit();
    protected Rabbit b = new Rabbit();

    @Test
    public void burrowReproductionTest() { doesBurrowReproduceCorrectly(a, b); };
    @Test //K1-2c "Kaniner lever af græs som de spiser i løbet af dagen,"
    public void hasEatenTest(){
        Grass g = new Grass();
        Home h = new Home(new Location(5,5),5,"Rabbit");
        w.setTile(startLocation,g);
        w.setTile(startLocation,a);
        a.setHome(w,h); //Sets home before to not get exception
        Object[] beforeEntities = w.getEntities().keySet().toArray();
        System.out.println(Arrays.toString(beforeEntities));
        for(int i = 0;g.getNutrition() > 0 || i > 20;i++){
            a.eat(w, g);
        }
        Object[] afterEntities = w.getEntities().keySet().toArray();
        System.out.println(Arrays.toString(afterEntities));
        Assert.assertFalse(Arrays.equals(beforeEntities,afterEntities));
    }
    @Test //k1-2f. Kaniner kan grave huller, eller dele eksisterende huller med andre kaniner
    // Kaniner kan kun være knyttet til et hul.
    public void hasDugBurrowTest(){
        w.setTile(startLocation,a);
        w.setNight();
        a.act(w);
        Home h = null;
        for(Object o: w.getEntities().keySet()){
            if(o instanceof Burrow){
                h = (Home) o;
                System.out.println("getAllowedSpecies(): " + h.getAllowedSpecies());
            }
        }
        Assert.assertNotNull(h);
    }
}