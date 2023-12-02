import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
public class Pack implements Actor,DynamicDisplayInformationProvider {
    private HashSet<Wolf> packList;
    private WolfBurrow wolfburrow;
    private final int maxSize;
    private final Location homeLocation;
    private Location attractionPoint;
    private Rabbit pray;
    private int foodToDistribute;
    public Pack(World w, Wolf wolf){
        this.packList = new HashSet<>();
        this.homeLocation = w.getLocation(wolf);
        packList.add(wolf);
        w.add(this);
        this.attractionPoint = homeLocation;
        this.foodToDistribute = 0;
        this.maxSize = 5;
    }
    @Override
    public void act(World w) {
        if(wolfburrow == null){
            this.wolfburrow = new WolfBurrow(); //TODO: make it so doesn't delete other burrows
            if(w.containsNonBlocking(homeLocation)) {
                w.delete(w.getNonBlocking(homeLocation));
            }
            w.setTile(homeLocation,wolfburrow);
        }
        if(foodToDistribute > 0){
            System.out.println("Food for all");
            int foodPrWolf = foodToDistribute / packList.size();
            foodToDistribute = 0;
            for(Wolf wolf : packList){
                wolf.setEnergy(wolf.getEnergy() + foodPrWolf);
            }
        }
        //Moves towards food, avoids other packs or moves randomly (in that order)
        if(w.isDay()){
            lookForPray(w,attractionPoint);
            if(pray != null){
                try {
                    if (w.isOnTile(pray)) {
                        attractionPoint = w.getLocation(pray);
                    } else {
                        pray = null;
                    }
                }
                catch (IllegalArgumentException ignore){
                    pray = null;
                } //If it is removed from the map
            }
            else {
                Location checker = attractionPoint;
                //Logic for avoid other packs
                for(Object o :w.getEntities().keySet()){
                    if(o instanceof Pack && ((Pack) o).gethomeLocation() != homeLocation){
                        if(w.getSurroundingTiles(attractionPoint,3).contains(((Pack) o).getAttractionPoint())){
                            int currentDifferenceXVal = Math.abs(((Pack) o).getAttractionPoint().getX() - attractionPoint.getX());
                            int currentDifferenceYVal = Math.abs(((Pack) o).getAttractionPoint().getY() - attractionPoint.getY());
                            int currentDifference = currentDifferenceXVal + currentDifferenceYVal;
                            for(Location l: w.getSurroundingTiles(attractionPoint)){
                                int differenceXVal = Math.abs(((Pack) o).getAttractionPoint().getX() - l.getX());
                                int differenceYVal = Math.abs(((Pack) o).getAttractionPoint().getY() - l.getY());
                                int totalDifference = differenceXVal + differenceYVal;
                                if(totalDifference > currentDifference){
                                    attractionPoint = l;
                                    currentDifference = totalDifference;
                                }
                            }
                        }
                    }
                }
                if(checker.equals(attractionPoint)) {
                    int randomInt = HelperMethods.getRandom().nextInt(w.getSurroundingTiles(attractionPoint).toArray().length);
                    attractionPoint = (Location) w.getSurroundingTiles(attractionPoint).toArray()[randomInt];
                }
            }
        }
        //Moves back home at night
        else if(w.isNight() && !attractionPoint.equals(homeLocation)) {
            attractionPoint = homeLocation;
        }
        for(Wolf wolf: packList){
            try {
                if (wolf.getIsAwake() && w.getLocation(wolf).equals(homeLocation) && w.isNight()) {
                    w.remove(wolf);
                    wolf.sleep();
                } else if (w.isDay() && !wolf.getIsAwake()) {
                    int radius = 3;
                    Location l = HelperMethods.getClosestEmptyTile(w, w.getLocation(wolfburrow), radius);
                    wolf.wakeUp();
                    w.setTile(l, wolf);
                } else if (wolf.getIsAwake()) {
                    wolf.moveToLocation(w, attractionPoint);
                }
            }
            catch (IllegalArgumentException ignore) {} //If the wolf dies at same point
        }
    }
    public boolean stillHasRoom(){
        return packList.size() < maxSize;
    }
    public void addToPack(Wolf wolf){
        packList.add(wolf);
        wolf.setPack(true);
    }
    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.blue, "wolf"); //Needs display information even if not on the map
    }
    public HashSet<Wolf> getPackList(){
        return packList;
    }
    public Location gethomeLocation(){
        return homeLocation;
    }
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
                pray = (Rabbit) w.getTile(loc);
            }
        }
    }
    public void addFood(int newFood){
        foodToDistribute += newFood;
    }
}
