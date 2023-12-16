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
    @Test
    public void spreadTest(){
        w.setTile(startLocation,c);
        w.setTile(new Location(1,1),cInfected);
        System.out.println(c.getIsInfected());
        disableSysOut(true);
        for(int i = 0;!c.getIsInfected() && i < 100;i++){
            w.step();
        }
        disableSysOut(false);
        System.out.println(c.getIsInfected());
        Assert.assertTrue(c.getIsInfected());
    }
}
