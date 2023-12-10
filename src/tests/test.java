package tests;

import animals.Animal;
import ediblesandflora.edibles.Carcass;
import ediblesandflora.edibles.Edible;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.Assert;
import org.junit.Test;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;

import static utils.HelperMethods.disableSysOut;

public class test {
    protected int worldSize = 4;
    protected Program p = new Program(worldSize, 800, 1);
    protected World w = p.getWorld();

    private void keepSatiated() {
        for (Object o : w.getEntities().keySet()) {
            if (o instanceof Animal) {
                ((Animal) o).setSatiation(100);
            }
        }
    }

    private Set<Object> getObjectsByType(String type) {
        Set<Object> objects = new HashSet<>();
        for (Object o: w.getEntities().keySet()) {
            if (o.getClass().getSimpleName().equals(type)) {
                objects.add(o);
            }
        }
        return objects;
    }
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
    protected void dieWithTime(Object a){
        w.setTile(startLocation,a);
        for(int i = 1;w.getEntities().containsKey(a);i++){
            System.out.println("Act nr: " + i);
            if(a instanceof Animal){
                ((Animal) a).act(w);
            }
            else if(a instanceof Edible){
                ((Edible) a).act(w);
            }
            if(i >= 200){
                break; //Break so it doesn't just go forever
            }
        }
        boolean hasType = false;
        System.out.println("Input animal simple name: " + a.getClass().getSimpleName());
        for(Object o: w.getEntities().keySet()){
            System.out.println("Entity exists simple name: " + o.getClass().getSimpleName());
            if(o.getClass().getSimpleName().equals(a.getClass().getSimpleName())){
                hasType = true;
            }
        }
        Assert.assertFalse(hasType);
    }
    protected void delete(Object o){
        w.setTile(startLocation,o);
        if(o instanceof Animal){
            ((Animal) o).delete(w);
        }
        else if(o instanceof Edible){
            ((Edible) o).delete(w);
        }
        try {
            Assert.assertFalse(w.contains(o));
        }
        catch (IllegalArgumentException iae) {Assert.assertFalse(false);} //Throws an exception instead of return false
    }
    protected void cantMoveWhenBlocked(Animal a){
        //Uses carcass to block movement
        Location[] locations = new Location[]{new Location(0, 1),new Location(1,1),new Location(1,0)};
        for(Location l: locations){
            String s = String.format("X: %s, Y: %s",l.getX(),l.getY());
            System.out.println(s);
            w.setTile(l,new Carcass(true));
        }
        int startEnergy = a.getEnergy();
        w.setTile(startLocation,a);
        a.act(w);
        Assert.assertEquals(startLocation, w.getLocation(a));
        Assert.assertEquals(startEnergy,a.getEnergy());
    }

    protected void doesBurrowReproduceCorrectly(Animal a, Animal b) {
        String animalType = a.getClass().getSimpleName();
        w.setTile(new Location(0,0), b);
        w.setTile(new Location(0,1), a);
        // Checking that there are only to animals of this type to start with
        Assert.assertEquals(2, getObjectsByType(animalType).size());
        w.setNight();
        disableSysOut(true);//Disable prints because it is annoying that it prints so much and it prints from itu library
        while (w.getCurrentTime() != 1) {
            p.simulate();
        }
        // Checking that animals didn't mate the first night (because they haven't matured yet)
        Assert.assertEquals(2, getObjectsByType(animalType).size());
        // Making them mature
        a.setAge(Animal.MATURITY_AGE);
        b.setAge(Animal.MATURITY_AGE);
        // Simulating until they can mate
        while (!a.canMate() || !b.canMate()) {
            p.simulate();
            keepSatiated();
        }
        w.setNight();
        while (w.getCurrentTime() != 1) {
            p.simulate();
            keepSatiated();
        }
        // Making sure they produced one offspring during the night
        Assert.assertEquals(3, getObjectsByType(animalType).size());
        Assert.assertFalse(a.canMate() || b.canMate());
        // Seeing if they can mate again after mating once
        while (!a.canMate() || !b.canMate()) {
            p.simulate();
            keepSatiated();
        }
        w.setNight();
        while (w.getCurrentTime() != 1) {
            p.simulate();
            keepSatiated();
        }
        disableSysOut(false); //Enable system.out.print again
        // Making sure the one more offspring was produced
        Assert.assertEquals(4, getObjectsByType(animalType).size());
    }

}
