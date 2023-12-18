package tests;

import animals.Animal;
import animals.Bear;
import animals.Rabbit;
import animals.packanimals.Fox;
import animals.packanimals.PackAnimal;
import animals.packanimals.Wolf;
import ediblesandflora.Fungus;
import ediblesandflora.edibles.BerryBush;
import ediblesandflora.edibles.Carcass;
import ediblesandflora.edibles.Edible;
import ediblesandflora.edibles.Grass;
import homes.Burrow;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static utils.HelperMethods.*;

import org.junit.jupiter.params.ParameterizedTest;
import utils.Config;
import utils.HelperMethods;

public class test {
    protected final int worldSize = 4;
    protected final World w = new World(worldSize);
    protected final Location startLocation = new Location(0,0);
    static Stream<PackAnimal> PackAnimals(){
        return Stream.of(
                new Wolf(),
                new Fox()
        );
    }
    static Stream<Animal> Animals() {
        Stream<Animal> remainingAnimals = Stream.of( new Rabbit(), new Bear());
        return Stream.concat(PackAnimals(),remainingAnimals);
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
    @Test
    public void canInputObjectsFromFile(){
        String input = "data/test-files/oneOfEach.txt";

        int size = HelperMethods.readWorldSize(input);
        int delay = 1000;
        int display_size = 800;

        Program p = new Program(size, display_size, delay);
        World w = p.getWorld();
        HelperMethods.readObjects(input, w, p);

        //Counter for amount of objects in the world
        HashMap<String, Integer> counterObjects = amountTypes(w);
        //Test that it meets predefined conditions
        Assertions.assertEquals(25,w.getSize());
        Assertions.assertTrue(counterObjects.get("Grass") >= 7 && counterObjects.get("Grass") <= 11);
        Assertions.assertEquals(2,counterObjects.get("Rabbit"));
        Assertions.assertEquals(1,counterObjects.get("Rabbit Burrow"));
        Assertions.assertEquals(2,counterObjects.get("Wolf"));
        Assertions.assertEquals(1,counterObjects.get("Pack"));
        Assertions.assertEquals(1,counterObjects.get("Bear"));
        Assertions.assertTrue(counterObjects.get("BerryBush") >= 1 && counterObjects.get("BerryBush") <= 3);
        Assertions.assertTrue(counterObjects.get("Carcass Fungi") >= 4 && counterObjects.get("Carcass Fungi") <= 5);
        Assertions.assertEquals(1,counterObjects.get("Carcass"));
        Assertions.assertTrue(counterObjects.get("Fox") >= 3 && counterObjects.get("Fox") <= 6);
    }
    private HashMap<String, Integer> amountTypes(World w){
        HashMap<String, Integer> counterObjects = new HashMap<>();
        for(Object o : w.getEntities().keySet()){
            String objectName;
            if(o instanceof Carcass){
                if(((Carcass) o).getIsInfected()){
                    objectName = o.getClass().getSimpleName() + " Fungi";
                }
                else{
                    objectName = o.getClass().getSimpleName();
                }
            }
            else if(o instanceof Burrow){
                objectName = ((Burrow) o).getAllowedSpecies() + " " + o.getClass().getSimpleName();
            }
            else {
                objectName = o.getClass().getSimpleName();
            }
            if(counterObjects.containsKey(objectName)){
                counterObjects.put(objectName,counterObjects.get(objectName) + 1);
            }
            else{
                counterObjects.put(objectName,1);
            }
        }
        for(String s : counterObjects.keySet()){
            System.out.println(s + " amount " + counterObjects.get(s));
        }
        return counterObjects;
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
        Location moveToLocation = new Location(w.getSize()+1,w.getSize()+1);
        Assertions.assertNotEquals(moveToLocation.getX(), startLocation.getX());
        Assertions.assertNotEquals(moveToLocation.getY(), startLocation.getY());
        assertThrows(IllegalArgumentException.class, () -> w.move(a,moveToLocation));
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
        Assertions.assertNotEquals(startEnergy, afterEnergy);
    }
    @ParameterizedTest
    @MethodSource("Animals")
    protected void moveToTest(Animal a){
        w.setTile(startLocation,a);
        Location[] locations = new Location[]{new Location(0, 1),new Location(1,1),new Location(1,0)};
        for(Location l: locations){
            String s = String.format("Carcass at X: %s, Y: %s",l.getX(),l.getY());
            System.out.println(s);
            w.setTile(l,new Carcass(false));
        }
        a.moveTo(w,new Location(3,3));
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
        Assertions.assertNotEquals(startSatiation, afterSatiation);
    }
    @ParameterizedTest
    @MethodSource("Objects")
    protected void dieWithTimeTest(Object o){
        w.setTile(startLocation,o);
        if(o instanceof Fox){
            w.setNight(); //Needs to be night for fox to act
        }
        for(int i = 0;w.getEntities().containsKey(o) && i < 200;i++){
            System.out.println("Act nr: " + i);
            invokeMethod(o,"act",w);
        }
        if(!(o instanceof BerryBush)){ //Berry bush cannot "die", so it should still be on the map
            assertThrows(IllegalArgumentException.class, () -> w.getLocation(o));
        }
        else{
            Assertions.assertTrue(w.contains(o));
        }
    }
    @ParameterizedTest
    @MethodSource("Objects")
    protected void deleteTest(Object o){
        System.out.println("Before being placed: " + w.contains(o));
        w.setTile(startLocation,o);
        System.out.println("After being placed: " + w.contains(o));
        invokeMethod(o,"delete",w);
        System.out.println("After invokeDelete: " + w.contains(o));
        if(!(o instanceof BerryBush)) {
            Assertions.assertFalse(w.contains(o));
        }
        else{
            Assertions.assertTrue(w.contains(o));
        }
    }
    @ParameterizedTest
    @MethodSource("Animals")
    protected void cantMoveWhenBlocked(Animal a){
        //Uses carcass to block movement
        Location[] locations = new Location[]{new Location(0, 1),new Location(1,1),new Location(1,0)};
        for(Location l: locations){
            String s = String.format("Carcass at X: %s, Y: %s",l.getX(),l.getY());
            System.out.println(s);
            w.setTile(l,new Carcass(false));
        }
        int startEnergy = a.getEnergy();
        w.setTile(startLocation,a);
        a.act(w);
        Assertions.assertEquals(startLocation, w.getLocation(a));
        Assertions.assertEquals(startEnergy, a.getEnergy());
    }

    protected void doesBurrowReproduceCorrectly(Animal a, Animal b) {
        String animalType = a.getClass().getSimpleName();
        w.setTile(new Location(0,0), b);
        w.setTile(new Location(0,1), a);
        // Checking that there are only to animals of this type to start with
        Assertions.assertEquals(2, getObjectsByType(animalType).size());
        w.setNight();
        System.out.println("0. while loop");
        while (w.getCurrentTime() != 1) {
            w.step();
        }
        // Checking that animals didn't mate the first night (because they haven't matured yet)
        Assertions.assertEquals(2, getObjectsByType(animalType).size());
        // Making them mature
        a.setAge(Animal.getMaturityAge());
        b.setAge(Animal.getMaturityAge());
        // Simulating until they can mate
        System.out.println("1. while loop");
        while (!a.canMate() || !b.canMate()) {
            w.step();
            keepSatiated();
        }
        w.setNight();
        System.out.println("2. while loop");
        while (w.getCurrentTime() != 1) {
            w.step();
            keepSatiated();
        }
        // Making sure they produced one offspring during the night
        Assertions.assertEquals(3, getObjectsByType(animalType).size());
        Assertions.assertFalse(a.canMate() || b.canMate());
        // Seeing if they can mate again after mating once
        System.out.println("3. while loop");
        while (!a.canMate() || !b.canMate()) {
            w.step();
            keepSatiated();
        }
        w.setNight();
        System.out.println("4. while loop");
        while (w.getCurrentTime() != 1) {
            w.step();
            keepSatiated();
        }
        // Making sure the one more offspring was produced
        Assertions.assertEquals(4, getObjectsByType(animalType).size());
    }
}
