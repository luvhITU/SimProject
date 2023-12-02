import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Wolf extends Animal implements Actor {
    boolean hasPack;
    Pack thePack;
    public Wolf(){
        super(Config.Wolf.DIET, Config.Wolf.NUTRITION, Config.Wolf.DAMAGE, Config.Wolf.ABSORPTION_PERCENTAGE);
        this.hasPack = false;
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.blue, "wolf");
    }
    @Override
    public void act(World w){
        super.act(w);
        if(!hasPack){
            //Checks if there is room in current packs
            for(Object o :w.getEntities().keySet()){
                if(o instanceof Pack){
                    if(((Pack) o).stillHasRoom()){
                        ((Pack) o).addToPack(this);
                        thePack = (Pack) o;
                    }
                }
            }
            //Checks if it hasn't been added to a pack and then creates a new one
            if(!hasPack){
                thePack = new Pack(w,this);
            }
        }
        try {
            if(w.isDay() && w.isOnTile(this)){
                tryAttack(w);
            }
        } catch (IllegalArgumentException ignore) {} //Dies sometimes before doing actions
        //TODO: copied from rabbit should maybe be moved to animal
        if (!getHasMatedToday() && w.getCurrentTime() > 0) { tryToMate(w); }
    }
    public void moveToLocation(World w,Location l) {
        Set<Location> neighbours = w.getEmptySurroundingTiles(w.getLocation(this));
        if (neighbours.isEmpty()) {throw new IllegalStateException("No empty tiles to move to");}

        try {
            Location currL = w.getLocation(this);
            if (currL.equals(l)) {
                return;
            }
            int minDistance = Integer.MAX_VALUE;
            Location bestMove = null;
            for (Location n : neighbours) {
                int nDistance = Math.abs(n.getX() - l.getX()) + Math.abs((n.getY() - l.getY()));
                if (nDistance < minDistance) {
                    minDistance = nDistance;
                    bestMove = n;
                }
            }
            if (bestMove != null) {
                w.move(this, bestMove);
            }
        } catch (IllegalArgumentException iae) {
            //System.out.println(iae.getMessage());
        }
    }
    public void tryAttack(World w){ //TODO: moved to animal since bears should use the same attack logic
        for(Location l : w.getSurroundingTiles()){
            if(w.getTile(l) != null) {
                for (Object diet : getDiet().toArray()) {
                    try {
                        if (diet.equals(w.getTile(l).getClass().getSimpleName())) {
                            Rabbit rabbit = (Rabbit) w.getTile(l);
                            if((rabbit.getEnergy() - rabbit.calcDamageTaken(this)) <= 0){
                                w.delete(rabbit);
                                thePack.addFood(rabbit.getNutrition());
                            }
                            else{
                                rabbit.setEnergy(rabbit.getEnergy() - rabbit.calcDamageTaken(this));
                            }

                        } else if (w.getTile(l) instanceof Animal) {
                            Animal animal = (Animal) w.getTile(l);
                            //Checks if wolf is a part of the same pack
                            if(!thePack.getPackList().contains(animal)){
                               animal.setEnergy(animal.getEnergy() - animal.calcDamageTaken(this));
                            }
                        }
                    }
                    catch (IllegalArgumentException ignore){ }
                }
            }
        }
    }
    public void setPack(boolean Boo){
        hasPack = Boo;
    }
}
