import itumulator.executable.DisplayInformation;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.*;
import java.util.HashSet;
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
//        System.out.println("Pack alpha is: " + pack.getTempAlpha() + "\n. Alpha is awake: " + pack.getTempAlpha().isAwake);
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
                    System.out.println(pack.getTarget());
                    System.out.println("Hunt triggered");
                    hunt(w, pack.getTarget());
                }
            }
        }
    }

    @Override
    public void delete(World w) {
        pack.remove(this);
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
        if (w.isOnTile(alpha)) {
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
        System.out.println("target is: " + target);
        if (target == null) { return true; }
        if (target instanceof Edible) {
            System.out.println("Target is edible");
            return ((Edible) target).isEdible();
        }
        return !(w.contains(target) && w.isOnTile(target));
    }

    public void findOrStartPack(World w) {
        System.out.println("Seeking Pack");
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
            System.out.println("Creating Pack");
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
                System.out.println("target unavailable");
                return;
            }
            if (wolf.isAwake) {
                wolf.joinHunt(w, target);
                wolf.stepAgeWhenPackActed = wolf.stepAge;
            }

        }
    }

    private void joinHunt(World w, Object target) {
        System.out.println("HEEEEEEY JOINHUNT");
        super.hunt(w, target);
    }

    private boolean hasPackActed() {
        return stepAgeWhenPackActed == stepAge;
    }
}