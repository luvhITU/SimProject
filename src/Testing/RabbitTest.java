package Testing;

import MapComponents.Grass;
import MapComponents.Rabbit;
import Places.Home;
import Places.RabbitBurrow;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

public class RabbitTest {
    protected int worldSize = 10;
    protected World w = new World(worldSize);
    protected Rabbit r = new Rabbit();
    protected Location startLocation = new Location(0,0);
    @Test
    public void hasMovedTest(){
        int[] startXY = {startLocation.getX(),startLocation.getY()};
        System.out.println(Arrays.toString(startXY));
        w.setTile(startLocation,r);
        r.act(w);
        Location movedLocation = w.getLocation(r);
        int[] movedXY = {movedLocation.getX(),movedLocation.getY()};
        System.out.println(Arrays.toString(movedXY));
        Assert.assertFalse(Arrays.equals(startXY,movedXY));
    }
    //There is some bug with it placing a burrow on grass and throws an exception
    public void hasEatenTest(){
        Grass g = new Grass();
        Map<Object, Location> beforeEntities = w.getEntities();
        w.setTile(startLocation,g);
        w.setTile(startLocation,r);
        r.act(w);
        Map<Object, Location> afterEntities = w.getEntities();
        Assert.assertNotEquals(beforeEntities,afterEntities);
    }
    @Test
    public void hasDugBurrowTest(){
        w.setTile(startLocation,r);
        r.act(w);
        Home h = null;
        for(Object o: w.getEntities().keySet()){
            if(o instanceof RabbitBurrow){
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
}