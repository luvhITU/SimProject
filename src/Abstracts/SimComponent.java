package Abstracts;

import itumulator.world.World;

import java.awt.*;

public abstract class SimComponent {
    /***
     * Getter for type
     * @return  String of the type
     */
    public String getType() {
        return getClass().getSimpleName();
    }
}








