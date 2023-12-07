package Places;

import MapComponents.Wolf;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.World;

import java.awt.*;
import java.util.*;

public class Pack implements DynamicDisplayInformationProvider {
    private Set<Wolf> packList;
    private Wolf tempAlpha;
    private WolfBurrow home;
    private final int maxSize;

    private Object target;

    public Pack(World w, Wolf wolf) {
        packList = new HashSet<>();
        tempAlpha = wolf;
        home = null;
        packList.add(wolf);
        w.add(this);
        this.maxSize = 5;
    }

    public boolean stillHasRoom() {
        return packList.size() < maxSize;
    }

    public void addToPack(Wolf wolf) {
        packList.add(wolf);
        wolf.storePack(this);
        home.add(wolf);
    }

    public void setHome(WolfBurrow home) {
        this.home = home;
    }

    public void remove(Wolf wolf) {
        packList.remove(wolf);
    }

    public Object getTarget() {
        return target;
    }

    public Wolf getTempAlpha() {
        return tempAlpha;
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.blue, "wolf"); //Needs display information even if not on the map
    }

    public Set<Wolf> getPackList() {
        return packList;
    }

    public WolfBurrow getHome() {
        return home;
    }

    public void setTempAlpha(Wolf wolf) {
        tempAlpha = wolf;
    }

    public void setTarget(Object target) {
        this.target = target;
    }
}
