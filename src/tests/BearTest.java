package tests;

import animals.Bear;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class BearTest extends test {
    Bear b = new Bear();
    @Test
    public void hasTerritory(){
        w.setTile(startLocation,b);
        b.beginAct(w);
            Assertions.assertNotNull(b.getHomeLocation());
    }
}
