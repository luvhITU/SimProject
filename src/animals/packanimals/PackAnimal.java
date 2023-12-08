package animals.packanimals;

import animals.Animal;
import ediblesandflora.edibles.Edible;
import homes.Home;
import itumulator.world.Location;
import itumulator.world.World;
import utils.HelperMethods;

import java.util.Set;

public class PackAnimal extends Animal {
    protected int maxPackSize;
    protected Pack pack;
    private int stepAgeWhenPackActed;

    public PackAnimal(Set<String> diet, int damage, int maxHealth, int maxSpeed, int matingCooldownDays, int maxPackSize) {
        super(diet, damage, maxHealth, maxSpeed, matingCooldownDays);
        this.maxPackSize = maxPackSize;
        stepAgeWhenPackActed = 0;
    }
    @Override
    public void act(World w) {
        if (pack == null) {
            findOrStartPack(w);
        }

        super.act(w);
        if (hasPackActed()) { return; }
        if (isAwake) {
            Location closestHostileWolfLoc = HelperMethods.findNearestLocationByType(w, w.getLocation(this), tilesInSight, this.getClass().getSimpleName());
            if (closestHostileWolfLoc != null) {
                flee(w, closestHostileWolfLoc);
            } else if (w.isNight()) {
                goHome(w);
            } else {
                if (isTargetUnavailable(w)) {
                    pack.setTarget(findClosestEdible(w));
                }
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
    public void setHome(World w, Home home) {
        if (pack.getPackHome() == null) {
            pack.setPackHome(w, home);
        } else {
            super.setHome(w, home);
        }
    }

    @Override
    public Set<Location> calcTilesInSight(World w) {
        return pack.calcPackTilesInSight(w);
    }

    protected Set<Location> calcPersonalTilesInSight(World w) {
        return super.calcTilesInSight(w);
    }

    private boolean isTargetUnavailable(World w) {
        Object target = pack.getTarget();
        if (target == null) { return true; }
        if (target instanceof Edible) {
            return !((Edible) target).isEdible();
        }
        return !(w.contains(target) && w.isOnTile(target));
    }

    public void findOrStartPack(World w) {
        //Checks if there is room in current packs
        for (Object o : w.getEntities().keySet()) {
            if (o instanceof Pack) {
                Pack pack = (Pack) o;
                if (!pack.isFull()) {
                    this.pack = pack;
                    pack.add(w, this);

                }
            }
        }
        if (pack == null) {
            pack = new Pack();
            pack.add(w, this);
            w.add(pack);
        }
    }

    @Override
    protected void hunt(World w, Object target) {
        for (PackAnimal member : pack.getMembers()) {
            if (isTargetUnavailable(w)) {
                return;
            }
            if (!(member.isDead() && member.hasPackActed()) && w.isOnTile(member)) {
                member.joinHunt(w, target);
                member.stepAgeWhenPackActed = member.stepAge;
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
