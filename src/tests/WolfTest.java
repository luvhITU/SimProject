package tests;

import animals.packanimals.Wolf;
import org.junit.Test;

public class WolfTest extends test{
    Wolf w = new Wolf();
    @Test
    public void hasMovedTest(){
        hasMoved(w);
    }
    @Test
    public void losesEnergyTest(){
        losesEnergy(w);
    }
    @Test
    public void losesSatiationTest(){
        losesSatiation(w);
    }
    @Test
    public void deleteTest(){
        delete(w);
    }

}
