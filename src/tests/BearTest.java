package tests;

import animals.Bear;
import org.junit.Test;

public class BearTest extends test {
    Bear b = new Bear();
    @Test
    public void hasMovedTest(){
        hasMoved(b);
    }
    @Test
    public void losesEnergyTest(){
        losesEnergy(b);
    }
    @Test
    public void losesSatiationTest(){
        losesSatiation(b);
    }
}
