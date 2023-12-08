package tests;

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

public class RabbitTest {
    protected int worldSize = 10;
    protected World w = new World(worldSize);
    protected Rabbit r = new Rabbit();
    protected Location startLocation = new Location(0,0);
    @Test
    public void hasMovedTest(){
        int[] startXY = {startLocation.getX(),startLocation.getY()};
        w.setTile(startLocation,r);
        r.act(w);
        Location movedLocation = w.getLocation(r);
        int[] movedXY = {movedLocation.getX(),movedLocation.getY()};
        Assert.assertFalse(Arrays.equals(startXY,movedXY));
    }
    @Test
    public void hasEatenTest(){
        Grass g = new Grass();
        Home h = new Home(new Location(5,5),5,"Rabbit");
        w.setTile(startLocation,g);
        w.setTile(startLocation,r);
        Object[] beforeEntities = w.getEntities().keySet().toArray();
        System.out.println(Arrays.toString(beforeEntities));
        r.setHome(w,h); //Sets home before to not get bug
        int i = 0;
        while(g.getNutrition() > 0 || i > 20) {
            r.eat(w, g);
            i++;
        }
        Object[] afterEntities = w.getEntities().keySet().toArray();
        System.out.println(Arrays.toString(afterEntities));
        Assert.assertFalse(Arrays.equals(beforeEntities,afterEntities));
    }
    @Test
    public void hasDugBurrowTest(){
        w.setTile(startLocation,r);
        r.act(w);
        Home h = null;
        for(Object o: w.getEntities().keySet()){
            if(o instanceof Burrow){
                h = (Home) o;
            }
        }
        Assert.assertNotNull(h);
    }
    @Test
    public void losesEnergy(){
        int startEnergy = r.getEnergy();
        w.setTile(startLocation,r);
        r.act(w);
        int afterEnergy = r.getEnergy();
        Assert.assertNotEquals(startEnergy,afterEnergy);
    }
    @Test
    public void losesSatiation(){
        int startSatiation = r.getSatiation();
        w.setTile(startLocation,r);
        r.act(w);
        int afterSatiation = r.getSatiation();
        Assert.assertNotEquals(startSatiation,afterSatiation);
    }
    //Mating does not work right now @Test
    public void hasMated(){
        Rabbit r2 = new Rabbit();
        Location startLocation2 = new Location(0,1);
        w.setTile(startLocation,r);
        w.setTile(startLocation2,r2);
    }
}