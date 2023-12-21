package tests;

import ediblesandflora.Fungus;
import ediblesandflora.edibles.Carcass;
import itumulator.world.Location;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class FungusTest extends test {
    Fungus f = new Fungus(2000);

    @Test
    public void placedFungusCanSpread(){
        w.setTile(startLocation,f);
        f.setSpreadRadius(10);
        Location carcassLocation = new Location(1,1);
        Carcass carcass = new Carcass(false);
        w.setTile(carcassLocation, carcass);
        for(int i = 0; i < 100 && !carcass.getIsInfected(); i++){
            w.step();
        }
        for(Object o: w.getEntities().keySet()){
            if(o instanceof Carcass){
                System.out.println(((Carcass) o).getIsInfected());
            }
        }
        Assertions.assertFalse(carcass.getIsInfected());
    }
}
