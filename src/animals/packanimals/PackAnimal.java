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
    protected boolean avoidsOtherPacks;
    public Pack pack;
    private int stepAgeWhenPackActed;

    /***
     * Constructor for Pack Animal, same as super but also has a "maxPackSize"
     * @param diet                  Set of strings of diet elements
     * @param damage                int
     * @param maxHealth             int
     * @param maxSpeed              int
     * @param matingCooldownDays    int, amount of steps before mating again
     * @param maxPackSize           int
     */
    public PackAnimal(Set<String> diet, int damage, int maxHealth, int maxSpeed, int matingCooldownDays, int maxPackSize, boolean avoidsOtherPacks) {
        super(diet, damage, maxHealth, maxSpeed, matingCooldownDays);
        this.maxPackSize = maxPackSize;
        this.avoidsOtherPacks = avoidsOtherPacks;
        stepAgeWhenPackActed = -1;
    }
    protected PackAnimal(Set<String> diet, int damage, int maxHealth, int maxSpeed, int matingCooldownDays, int maxPackSize, boolean avoidsOtherPacks, boolean isNocturnal) {
        super(diet, damage, maxHealth, maxSpeed, matingCooldownDays, isNocturnal);
        this.maxPackSize = maxPackSize;
        this.avoidsOtherPacks = avoidsOtherPacks;
        stepAgeWhenPackActed = -1;
    }

    /***
     * See super
     * @param w providing details of the position on which the actor is currently located and much more.
     */
    @Override
    public void beginAct(World w) {
        super.beginAct(w);
        if (pack == null) {
            findOrStartPack(w);
        }
    }

    /***
     * See super
     * @param w World
     */
    @Override
    public void awakeAct(World w) {
        if (hasPackActed()) { return; }
        super.awakeAct(w);
        if (avoidsOtherPacks) {
            Location closestPackLoc = HelperMethods.findNearestLocationByType(w, w.getLocation(this), tilesInSight, this.getClass().getSimpleName());
            if (closestPackLoc != null) {
                flee(w, closestPackLoc);
                return;
            }
        } if (home == null || !isBedTime(w)) {
            if (home == null && isBedTime(w)) {
                sleep(w);
            }
            if (isTargetUnavailable(w)) {
                pack.setTarget(findTarget(w));
            }
            if (pack.getTarget() == null) {
                randomMove(w);
            } else {
                hunt(w, pack.getTarget());
            }
        } else {
            goHome(w);
        }
    }

    /***
     * See super
     * @param w World
     */
    @Override
    public void delete(World w) {
        if (pack != null) {
            pack.remove(this);
        }
        super.delete(w);
    }

    /***
     * See super
     * @param w     World
     * @param home  Home
     */
    @Override
    public void setHome(World w, Home home) {
        if (pack.getPackHome() == null) {
            pack.setPackHome(w, home);
        } else {
            super.setHome(w, home);
        }
    }

    /***
     * See super
     * @param w World
     * @return
     */
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

    /***
     * Searches world entities for packs and if any of the existing still has room, it gets added to that pack. If no
     * packs have room then it will make a new pack and add this.
     * @param w World
     */
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
                member.stepAgeWhenPackActed = (member == this) ? this.stepAge : member.stepAge + 1;
            }
        }
    }

    /***
     * Sets pack to input pack
     * @param p Pack for the fox
     */
    public void setPack(Pack p){
        this.pack = p;
    }
    private void joinHunt(World w, Object target) {
        super.hunt(w, target);
    }

    private boolean hasPackActed() {
        return stepAgeWhenPackActed == stepAge;
    }
}