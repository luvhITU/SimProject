package tests;

import animals.Bear;
import animals.Rabbit;
import ediblesandflora.edibles.Carcass;
import itumulator.world.Location;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class CarcassTest extends test{
    Carcass c = new Carcass(new Rabbit(),false);
    Carcass cInfected = new Carcass(new Bear(),true);
    @Test
    public void hasNotMovedTest(){
        w.setTile(startLocation,c);
        int[] startLocation = {w.getLocation(c).getX(),w.getLocation(c).getY()};
        System.out.println(Arrays.toString(startLocation));
        c.act(w);
        int[] endLocation = {w.getLocation(c).getX(),w.getLocation(c).getY()};
        System.out.println(Arrays.toString(endLocation));
        //w.setTile(new Location(1,1),c); To see if it could fail
        Assert.assertArrayEquals(startLocation,endLocation);
    }
}
