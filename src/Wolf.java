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
    boolean hasPack;
    Pack thePack;
    public Wolf(){
        super(Config.Wolf.DIET, Config.Wolf.NUTRITION, Config.Wolf.DAMAGE, Config.Wolf.HEALTH, Config.Wolf.SPEED);
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
        if (w.isDay() && w.isOnTile(this)) {
            hunt(w);
        }
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
    public void setPack(boolean Boo){
        hasPack = Boo;
    }
    public void storePack(Pack newPack){ this.thePack = newPack; }
    @Override
    protected Set<Object> findEdibles(World w) {
        Set<Object> edibles = new HashSet<>();
        Set<Location> friendlyExcludedTiles = thePack.getFriendlyExcludedTiles(w, this);

        for (Location l : friendlyExcludedTiles) {
            Object o = w.getTile(l);
            if (o != null && diet.contains(o.getClass().getSimpleName())) {
                if (o instanceof Edible && ((Edible) o).isEdible() || o instanceof Animal)
                    edibles.add(o);
                }
            }
        return edibles;
    }

    public void eat(World w, Edible edible) {
        int missingSatiation = (MAX_SATIATION - satiation) / thePack.getPackList().size();
        int edibleNutrition = edible.getNutrition();
        setSatiation(satiation + calcNutritionAbsorbed(edibleNutrition));
        edible.setNutrition(edibleNutrition - missingSatiation);
        if (edible.getNutrition() <= 0) {
            edible.delete(w);
        }
    }
}
