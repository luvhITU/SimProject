import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.*;
import java.util.Set;

public class Wolf extends Animal implements Actor {
    private Pack pack;
    int stepAgeWhenPackActed;

    public Wolf() {
        super(Config.Wolf.DIET, Config.Wolf.DAMAGE, Config.Wolf.HEALTH, Config.Wolf.SPEED, Config.Wolf.MATING_COOLDOWN_DAYS);
        stepAgeWhenPackActed = 0;
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.blue, "wolf");
    }

    @Override
    public void act(World w) {
        if (pack == null) {
            findOrStartPack(w);
        }
        if (isTargetUnavailable(w)) {
            pack.setTempAlpha(this);
        }

        super.act(w);
        if (hasPackActed()) { return; }
        if (isAwake) {
            Location closestHostileWolfLoc = HelperMethods.findNearestLocationByType(w, w.getLocation(this), tilesInSight, "Wolf");
            if (closestHostileWolfLoc != null) {
                flee(w, closestHostileWolfLoc);
            } else if (w.isNight()) {
                goHome(w);
            } else {
                pack.setTarget(findClosestEdible(w));
                if (pack.getTarget() == null) {
                    randomMove(w);
                } else {
                    hunt(w, pack.getTarget());
                }
            }
        }
        if (isDead()) { delete(w); }
    }

    @Override
    public void delete(World w) {
        if (pack != null) {
            pack.remove(this);
        }
        super.delete(w);
    }

    @Override
    public void digBurrow(World w, Home burrow) {
        super.digBurrow(w, burrow);
        pack.setHome((WolfBurrow) burrow);
    }

    @Override
    public Set<Location> calcTilesInSight(World w) {
        Set<Location> tilesInSight;
        Wolf alpha = pack.getTempAlpha();
        if (!alpha.isDead() && w.isOnTile(alpha)) {
            tilesInSight = w.getSurroundingTiles(w.getLocation(alpha), VISION_RANGE);
        } else {
            tilesInSight = super.calcTilesInSight(w);
        }
        for (Wolf wolf : pack.getPackList()) {
            if (w.isOnTile(wolf)) {
                tilesInSight.remove(w.getLocation(wolf));
            }
        }
        return tilesInSight;
    }

    private boolean isTargetUnavailable(World w) {
        Object target = pack.getTarget();
        if (target == null) { return true; }
        if (target instanceof Edible) {
            return ((Edible) target).isEdible();
        }
        return !(w.contains(target) && w.isOnTile(target));
    }

    public void findOrStartPack(World w) {
        //Checks if there is room in current packs
        for (Object o : w.getEntities().keySet()) {
            if (o instanceof Pack) {
                if (((Pack) o).stillHasRoom()) {
                    ((Pack) o).addToPack(this);
                    pack = (Pack) o;
                    home = pack.getHome();
                }
            }
        }
        //Checks if it hasn't been added to a pack and then creates a new one
        if (pack == null) {
            pack = new Pack(w, this);
            digBurrow(w, new WolfBurrow());
        }
    }

    public void storePack(Pack newPack) {
        this.pack = newPack;
    }

    @Override
    public int calcMissingSatiation() {
        return (int) Math.round((MAX_SATIATION - satiation) / (1.0 * pack.getPackList().size()));
    }

    @Override
    protected void hunt(World w, Object target) {
        for (Wolf wolf : pack.getPackList()) {
            if (isTargetUnavailable(w)) {
                return;
            }
            if (!wolf.isDead() && wolf.isAwake && !wolf.hasPackActed()) {
                wolf.joinHunt(w, target);
                wolf.stepAgeWhenPackActed = wolf.stepAge;
            }

        }
    }

    private void joinHunt(World w, Object target) {
        super.hunt(w, target);
    }

    private boolean hasPackActed() {
        return stepAgeWhenPackActed == stepAge;
    }
}