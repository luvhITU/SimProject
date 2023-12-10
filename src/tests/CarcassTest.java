package tests;

import animals.Bear;
import animals.Rabbit;
import ediblesandflora.edibles.Carcass;
import itumulator.world.Location;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static utils.HelperMethods.disableSysOut;

public class CarcassTest extends test{
    Carcass c = new Carcass(new Rabbit(),false);
    Carcass cInfected = new Carcass(new Bear(),true);
    @Test //Should be removed, since it doesn't matter
    public void hasNotMovedTest(){
        w.setTile(startLocation,c);
        int[] startLocation = {w.getLocation(c).getX(),w.getLocation(c).getY()};
        System.out.println(Arrays.toString(startLocation));
        c.act(w);
        //w.move(c,new Location(1,1)); To see if could fail
        int[] endLocation = {w.getLocation(c).getX(),w.getLocation(c).getY()};
        System.out.println(Arrays.toString(endLocation));
        Assert.assertArrayEquals(startLocation,endLocation);
    }
    @Test
    public void degradeTest(){
        dieWithTime(c);
    }
    @Test //Make sure infected carcass breaks down faster
    public void breakDownTime(){
        Carcass c2 = new Carcass(new Rabbit(), true);
        w.setTile(startLocation,c);
        w.setTile(new Location(1,1),c2);
        System.out.println("w.getEntities().keySet()");
        //disableSysOut(true);
        while(w.getEntities().containsKey(c2) && w.getEntities().containsKey(c)){
            p.simulate();
            System.out.println(w.getEntities().keySet());
        }
        disableSysOut(false);
    }
}
