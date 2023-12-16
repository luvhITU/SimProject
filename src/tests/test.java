package tests;

import animals.Animal;
import animals.Bear;
import animals.Rabbit;
import animals.packanimals.Fox;
import animals.packanimals.Wolf;
import ediblesandflora.Fungus;
import ediblesandflora.edibles.BerryBush;
import ediblesandflora.edibles.Carcass;
import ediblesandflora.edibles.Edible;
import ediblesandflora.edibles.Grass;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static utils.HelperMethods.disableSysOut;
import org.junit.jupiter.params.ParameterizedTest;

public class test {
    protected final int worldSize = 4;
    protected final World w = new World(worldSize);
    protected final Location startLocation = new Location(0,0);
    static Stream<Animal> Animals() {
        return Stream.of(
                new Rabbit(),
                new Wolf(),
                new Bear(),
                new Fox()
        );
    }
    static Stream<Edible> Edibles(){
        return Stream.of(
                new Grass(),
                new BerryBush(),
                new Carcass(false)
        );
    }
    static Stream<Fungus> Fungus(){
        return Stream.of(
                new Fungus(new Rabbit().getEnergy())
        );
    }
    static Stream<Object> AnimalsEdibles() {
        return Stream.concat(Animals(),Edibles());
    }
    static Stream<Object> Objects(){
        return Stream.concat(AnimalsEdibles(),Fungus());
    }
    @ParameterizedTest
    @MethodSource("Animals")
    public void canMovePositive(Animal a){
        w.setTile(startLocation,a);
        Location moveToLocation = new Location(2,2);
        Assertions.assertNotEquals(moveToLocation.getX(), startLocation.getX());
        Assertions.assertNotEquals(moveToLocation.getY(), startLocation.getY());
        w.move(a,moveToLocation);
    }
    @ParameterizedTest
    @MethodSource("Animals")
    public void canMoveNegative(Animal a){
        w.setTile(startLocation,a);
        Location moveToLocation = new Location(100,100);
        Assertions.assertNotEquals(moveToLocation.getX(), startLocation.getX());
        Assertions.assertNotEquals(moveToLocation.getY(), startLocation.getY());
        assertThrows(IllegalArgumentException.class, () -> {
            w.move(a,moveToLocation);
        });
    }
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
    @ParameterizedTest
    @MethodSource("Animals")
    protected void hasMoved(Animal a){
        int[] startXY = {startLocation.getX(),startLocation.getY()};
        System.out.println(Arrays.toString(startXY));
        w.setTile(startLocation,a);
        a.act(w);
        Location movedLocation = w.getLocation(a);
        int[] movedXY = {movedLocation.getX(),movedLocation.getY()};
        System.out.println(Arrays.toString(movedXY));
        Assertions.assertFalse(Arrays.equals(startXY,movedXY));
    }
    @ParameterizedTest
    @MethodSource("Animals")
    protected void losesEnergy(Animal a){
        System.out.println(a);
        int startEnergy = a.getEnergy();
        System.out.println(startEnergy);
        w.setTile(startLocation,a);
        a.act(w);
        int afterEnergy = a.getEnergy();
        System.out.println(afterEnergy);
        Assert.assertNotEquals(startEnergy,afterEnergy);
    }
    @ParameterizedTest
    @MethodSource("Animals")
    protected void losesSatiation(Animal a){
        int startSatiation = a.getSatiation();
        System.out.println(startSatiation);
        w.setTile(startLocation,a);
        a.act(w);
        int afterSatiation = a.getSatiation();
        System.out.println(afterSatiation);
        Assert.assertNotEquals(startSatiation,afterSatiation);
    }
    @ParameterizedTest
    @MethodSource("Objects")
    protected void dieWithTime(Object o){
        w.setTile(startLocation,o);
        for(int i = 0;w.getEntities().containsKey(o) && i < 200;i++){
            System.out.println("Act nr: " + i);
            if(o instanceof Animal){
                ((Animal) o).act(w);
            }
            else if(o instanceof Edible){
                ((Edible) o).act(w);
            }
            else if(o instanceof Fungus){
                ((Fungus) o).act(w);
            }
        }
        if(!(o instanceof BerryBush)){ //Berry bush cannot get "die"
            assertThrows(IllegalArgumentException.class, () -> {
                w.getLocation(o);
            });
        }
        else{
            Assertions.assertTrue(w.contains(o));
        }
    }
    @ParameterizedTest
    @MethodSource("Objects")
    protected void delete(Object o){
        w.setTile(startLocation,o);
        if(o instanceof Animal){
            ((Animal) o).delete(w);
        }
        else if(o instanceof Edible){
            ((Edible) o).delete(w);
        }
        else if(o instanceof Fungus){
            ((Fungus) o).delete(w);
        }
        System.out.println(w.contains(o));
        if(!(o instanceof BerryBush)) {
            Assertions.assertFalse(w.contains(o));
        }
        else{
            Assertions.assertTrue(w.contains(o));
        }
    }
    protected void cantMoveWhenBlocked(Animal a){
        //Uses carcass to block movement
        Location[] locations = new Location[]{new Location(0, 1),new Location(1,1),new Location(1,0)};
        for(Location l: locations){
            String s = String.format("X: %s, Y: %s",l.getX(),l.getY());
            System.out.println(s);
            w.setTile(l,new Carcass(false));
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
            w.step();
        }
        // Checking that animals didn't mate the first night (because they haven't matured yet)
        Assert.assertEquals(2, getObjectsByType(animalType).size());
        // Making them mature
        a.setAge(Animal.getMaturityAge());
        b.setAge(Animal.getMaturityAge());
        // Simulating until they can mate
        while (!a.canMate() || !b.canMate()) {
            w.step();
            keepSatiated();
        }
        w.setNight();
        while (w.getCurrentTime() != 1) {
            w.step();
            keepSatiated();
        }
        // Making sure they produced one offspring during the night
        Assert.assertEquals(3, getObjectsByType(animalType).size());
        Assert.assertFalse(a.canMate() || b.canMate());
        // Seeing if they can mate again after mating once
        while (!a.canMate() || !b.canMate()) {
            w.step();
            keepSatiated();
        }
        w.setNight();
        while (w.getCurrentTime() != 1) {
            w.step();
            keepSatiated();
        }
        disableSysOut(false); //Enable system.out.print again
        // Making sure the one more offspring was produced
        Assert.assertEquals(4, getObjectsByType(animalType).size());
    }

}
