import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Pack implements DynamicDisplayInformationProvider {
    private List<Wolf> packList;
    private Wolf alpha;
    private WolfBurrow wolfburrow;
    private final int maxSize;
//    private final Location homeLocation;
    private Location attractionPoint;
    private Object target;
    private int foodToDistribute;
    public Pack(World w, Wolf wolf){
        this.packList = new ArrayList<>();
        alpha = wolf;
        createWolfBurrow(w);
        packList.add(wolf);
        w.add(this);
//        this.attractionPoint = homeLocation;
        this.foodToDistribute = 0;
        this.maxSize = 5;
    }
//    @Override
//    public void act(World w) {
//        System.out.println(packList.toArray().length);
//        if(wolfburrow == null){
//            this.wolfburrow = new WolfBurrow(); //TODO: make it so doesn't delete other burrows
//            if(w.containsNonBlocking(homeLocation)) {
//                w.delete(w.getNonBlocking(homeLocation));
//            }
//            w.setTile(homeLocation,wolfburrow);
//        }
//        if(foodToDistribute > 0){
//            System.out.println("Food for all");
//            int foodPrWolf = foodToDistribute / packList.size();
//            foodToDistribute = 0;
//            for(Wolf wolf : packList){
//                wolf.setEnergy(wolf.getEnergy() + foodPrWolf);
//            }
//        }
//        //Moves towards food, avoids other packs or moves randomly (in that order)
//        if(w.isDay()){
//            lookForPray(w,attractionPoint);
//            if(target != null){
//                try {
//                    if (w.isOnTile(target)) {
//                        attractionPoint = w.getLocation(target);
//                    } else {
//                        target = null;
//                    }
//                }
//                catch (IllegalArgumentException ignore){
//                    target = null;
//                } //If it is removed from the map
//            }
//            else {
//                Location checker = attractionPoint;
//                //Logic for avoid other packs
//                for(Object o :w.getEntities().keySet()){
//                    if(o instanceof Pack && ((Pack) o).gethomeLocation() != homeLocation){
//                        if(w.getSurroundingTiles(attractionPoint,3).contains(((Pack) o).getAttractionPoint())){
//                            int currentDifferenceXVal = Math.abs(((Pack) o).getAttractionPoint().getX() - attractionPoint.getX());
//                            int currentDifferenceYVal = Math.abs(((Pack) o).getAttractionPoint().getY() - attractionPoint.getY());
//                            int currentDifference = currentDifferenceXVal + currentDifferenceYVal;
//                            for(Location l: w.getSurroundingTiles(attractionPoint)){
//                                int differenceXVal = Math.abs(((Pack) o).getAttractionPoint().getX() - l.getX());
//                                int differenceYVal = Math.abs(((Pack) o).getAttractionPoint().getY() - l.getY());
//                                int totalDifference = differenceXVal + differenceYVal;
//                                if(totalDifference > currentDifference){
//                                    attractionPoint = l;
//                                    currentDifference = totalDifference;
//                                }
//                            }
//                        }
//                    }
//                }
//                if(checker.equals(attractionPoint)) {
//                    int randomInt = HelperMethods.getRandom().nextInt(w.getSurroundingTiles(attractionPoint).toArray().length);
//                    attractionPoint = (Location) w.getSurroundingTiles(attractionPoint).toArray()[randomInt];
//                }
//            }
//        }
        //Moves back home at night
//        else if(w.isNight() && !attractionPoint.equals(homeLocation)) {
//            attractionPoint = homeLocation;
//        }
//        for(Wolf wolf: packList){
//            try {
//                if (wolf.getIsAwake() && w.getLocation(wolf).equals(homeLocation) && w.isNight()) {
//                    w.remove(wolf);
//                    wolf.sleep(w);
//                } else if (w.isDay() && !wolf.getIsAwake()) {
//                    int radius = 3;
//                    Location l = HelperMethods.getClosestEmptyTile(w, w.getLocation(wolfburrow), radius);
//                    wolf.wakeUp(w);
//                    w.setTile(l, wolf);
//                } else if (wolf.getIsAwake()) {
//                    wolf.moveTo(w, attractionPoint);
//                }
//            }
//            catch (IllegalArgumentException ignore) {} //If the wolf dies at same point
//        }
//    }
    public boolean stillHasRoom(){
        return packList.size() < maxSize;
    }
    public void addToPack(Wolf wolf){
        packList.add(wolf);
        wolf.storePack(this);
        wolfburrow.add(wolf);
    }

    private void createWolfBurrow(World w) {
        Location target = null;
        Location currL = w.getCurrentLocation();
        if (!(w.containsNonBlocking(currL) && w.getNonBlocking(currL) instanceof Home)) {
            target = currL;
        } else {
            Set<Location> neighbours = HelperMethods.getEmptySurroundingTiles(w, 5);
            neighbours.removeIf(n -> w.containsNonBlocking(n) && w.getNonBlocking(n) instanceof Home);
            target = HelperMethods.findNearestLocationByType(w, w.getCurrentLocation(), neighbours, "Location");
        }
        wolfburrow = new WolfBurrow();
        w.setTile(target, wolfburrow);
        wolfburrow.add((Animal) w.getTile(currL));
    }

    public void remove(Wolf wolf) {
        packList.remove(wolf);
        if (alpha == wolf) {
            alpha = packList.get(0);
        }
    }

    public Wolf getAlpha() {
        return alpha;
    }
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.blue, "INVISIBILITY"); //Needs display information even if not on the map
    }
    public List<Wolf> getPackList(){
        return packList;
    }
//    public Location gethomeLocation(){
//        return homeLocation;
//    }
    public Location getAttractionPoint(){
        return attractionPoint;
    }
    public WolfBurrow getWolfburrow(){
        return wolfburrow;
    }
    public void lookForPray(World w,Location l){
        int visionRadius = 4;
        Set<Location> fieldOfView = w.getSurroundingTiles(l, visionRadius);
        for(Location loc: fieldOfView){
            if(w.getTile(loc) instanceof Rabbit){
                target = (Rabbit) w.getTile(loc);
            }
        }
    }
    public void addFood(int newFood){
        foodToDistribute += newFood;
    }


}
