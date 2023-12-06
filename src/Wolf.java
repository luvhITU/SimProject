import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Wolf extends Animal implements Actor {
    Pack thePack;
    public Wolf(){
        super(Config.Wolf.DIET, Config.Wolf.DAMAGE, Config.Wolf.HEALTH, Config.Wolf.SPEED, Config.Wolf.MATING_COOLDOWN_DAYS);
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.blue, "wolf");
    }
    @Override
    public void act(World w){
        if(thePack == null){
            seekPack(w);
        }
        super.act(w);
        System.out.println("Pack alpha is: " + thePack.getAlpha() + "\n. Alpha is awake: " + thePack.getAlpha().isAwake);
        if (isAwake) {
            Location closestHostileWolfLoc = HelperMethods.findNearestLocationByType(w, w.getLocation(this), tilesInSight, "Wolf");
            if (closestHostileWolfLoc != null) {
                flee(w, closestHostileWolfLoc);
            } else if (w.isNight()) {
                goHome(w);
            } else {
//                updateAlphasTilesInSight(w);
                hunt(w);
            }
        }
    }

    @Override
    public void delete(World w) {
        thePack.remove(this);
        super.delete(w);
    }

    //    public void tryAttack(World w){ //TODO: need to be put in animal and change the "thePack" for it to work with bears
//        for(Location l : w.getSurroundingTiles()){
//            if(w.getTile(l) != null) {
//                for (Object diet : diet.toArray()) {
//                    try {
//                        if (diet.equals(w.getTile(l).getClass().getSimpleName())) {
//                            //TODO: needs to be changed from rabbit to just animal for it work with bears
//                            Rabbit rabbit = (Rabbit) w.getTile(l);
//                            attack(w, rabbit);
//                            if (rabbit.isDead()) {
//                                // TODO Carcass. Also try to use a variation of eat() method, because that's where nutrition absorbed is calculated
////                                thePack.addFood(rabbit.getNutrition());
//                            }
//                        } else if (w.getTile(l) instanceof Animal) {
//                            Animal animal = (Animal) w.getTile(l);
//                            //Checks if wolf is a part of the same pack
//                            if(!thePack.getPackList().contains(animal)){
//                               animal.setEnergy(animal.getEnergy() - animal.calcDamageTaken(this));
//                            }
//                        }
//                    }
//                    catch (IllegalArgumentException ignore){ }
//                }
//            }
//        }
//    }


    @Override
    public Set<Location> calcTilesInSight(World w) {
        Set<Location> tiles = new HashSet<>();
        Wolf alpha = thePack.getAlpha();
        if (w.isOnTile(alpha)) {
            tiles.addAll(w.getSurroundingTiles(w.getLocation(alpha), VISION_RANGE));
        } else {
            tiles.addAll(super.calcTilesInSight(w));
        }
        for (Wolf wolf : thePack.getPackList()) {
            if (w.isOnTile(wolf)) {
                tiles.remove(w.getLocation(wolf));
            }
        }
        return tiles;
    }

    public void seekPack(World w) {
        System.out.println("Seeking Pack");
        //Checks if there is room in current packs
        for(Object o : w.getEntities().keySet()){
            if(o instanceof Pack){
                if(((Pack) o).stillHasRoom()){
                    ((Pack) o).addToPack(this);
                    thePack = (Pack) o;
                }
            }
        }
        //Checks if it hasn't been added to a pack and then creates a new one
        if(thePack == null){
            System.out.println("Creating Pack");
            thePack = new Pack(w,this);
        }
        home = thePack.getWolfburrow();
    }

    // Might not be necessary
    public void updateAlphasTilesInSight(World w) {
        thePack.getAlpha().tilesInSight = w.getSurroundingTiles(5);
    }
    public void storePack(Pack newPack){ this.thePack = newPack; }
//    @Override
//    protected Set<Object> findEdibles(World w) {
//        Set<Object> edibles = new HashSet<>();
//        Wolf alpha = thePack.getAlpha();
//        Set<Location> friendlyExcludedTiles = alpha.getFriendlyExcludedTiles(w);
//
//        for (Location l : friendlyExcludedTiles) {
//            Object o = w.getTile(l);
//            if (o != null && diet.contains(o.getClass().getSimpleName())) {
//                if (o instanceof Edible && ((Edible) o).isEdible() || o instanceof Animal)
//                    edibles.add(o);
//                }
//            }
//        return edibles;
//    }
    @Override
    protected Object findClosestEdible(World w) {
        return HelperMethods.findNearestOfObjects(w, w.getLocation(thePack.getAlpha()), findEdibles(w));
    }

    public void eat(World w, Edible edible) {
        int missingSatiation = (int) Math.round((MAX_SATIATION - satiation) / (1.0 * thePack.getPackList().size()));
        int edibleNutrition = edible.getNutrition();
        setSatiation(satiation + calcNutritionAbsorbed(edibleNutrition));
        edible.setNutrition(edibleNutrition - missingSatiation);
        if (edible.getNutrition() <= 0) {
            edible.delete(w);
        }
    }

//    protected Set<Location> getFriendlyExcludedTiles(World w) {
//        Set<Location> friendlyExcludedTiles = new HashSet<>(tilesInSight);
//        for (Wolf packWolf: thePack.getPackList()) {
//            friendlyExcludedTiles.remove(w.getLocation(packWolf));
//        }
//        return friendlyExcludedTiles;
//    }
}
