package tests;

import animals.Animal;
import animals.Rabbit;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class test {
    protected int worldSize = 10;
    protected World w = new World(worldSize);
    protected Location startLocation = new Location(0,0);
    protected void hasMoved(Animal a){
        int[] startXY = {startLocation.getX(),startLocation.getY()};
        System.out.println(Arrays.toString(startXY));
        w.setTile(startLocation,a);
        a.act(w);
        Location movedLocation = w.getLocation(a);
        int[] movedXY = {movedLocation.getX(),movedLocation.getY()};
        System.out.println(Arrays.toString(movedXY));
        Assert.assertFalse(Arrays.equals(startXY,movedXY));
    }
    protected void losesEnergy(Animal a){
        int startEnergy = a.getEnergy();
        System.out.println(startEnergy);
        w.setTile(startLocation,a);
        a.act(w);
        int afterEnergy = a.getEnergy();
        System.out.println(afterEnergy);
        Assert.assertNotEquals(startEnergy,afterEnergy);
    }
    protected void losesSatiation(Animal a){
        int startSatiation = a.getSatiation();
        System.out.println(startSatiation);
        w.setTile(startLocation,a);
        a.act(w);
        int afterSatiation = a.getSatiation();
        System.out.println(afterSatiation);
        Assert.assertNotEquals(startSatiation,afterSatiation);
    }
    protected void dieOfHunger(Animal a){
        w.setTile(startLocation,a);
        for(int i = 1;w.getEntities().containsKey(a);i++){
            System.out.println("Act nr: " + i);
            a.act(w);
        }
        boolean hasAnimalOfType = false;
        System.out.println("Input animal simple name: " + a.getClass().getSimpleName());
        for(Object o: w.getEntities().keySet()){
            System.out.println("Entity exists simple name: " + o.getClass().getSimpleName());
            if(o.getClass().getSimpleName().equals(a.getClass().getSimpleName())){
                hasAnimalOfType = true;
            }
        }
        Assert.assertFalse(hasAnimalOfType);
    }
    protected void delete(Object o){
        w.setTile(startLocation,o);
        if(o instanceof Animal){
            ((Animal) o).delete(w);
        }
        try {
            Assert.assertFalse(w.contains(o));
        }
        catch (IllegalArgumentException iae) {Assert.assertFalse(false);} //Throws an exception instead of return false
    }
}
