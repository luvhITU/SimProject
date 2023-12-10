package tests;

import ediblesandflora.Fungus;
import org.junit.Test;

public class FungusTest extends test {
    Fungus f = new Fungus(100);
    @Test
    public void deleteTest(){
        delete(f);
    }
    @Test
    public void dieWithTimeTest() {
        dieWithTime(f);
    }
}
