import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

public class Rabbit extends Animal implements Actor {

    private static final int FIND_HOME_THRESHOLD = 8;

    public Rabbit() {
        super(Config.Rabbit.DIET, Config.Rabbit.NUTRITION, Config.Rabbit.DAMAGE, Config.Rabbit.ABSORPTION_PERCENTAGE);
    }

    @Override
    public void act(World w) {
        if (getIsDead(w)) {
            return;
        }
        super.act(w);
        if (!getIsAwake() && w.getCurrentTime() == 0) {
            emerge(w);
        }
        if (getIsAwake()) {
            doMovementPackage(w);
            if (!getHasMatedToday() && w.getCurrentTime() > 0) { tryToMate(w); }
            tryToEat(w);
        }

    }

//    @Override
//    public DisplayInformation getInformation() {
//        return new DisplayInformation(Color.blue, "rabbit-large");
//    }

    private void doMovementPackage(World w) {
        // If it isn't bedtime, move around.
        if (w.getCurrentTime() < FIND_HOME_THRESHOLD) {
            tryRandomMove(w);
            return;
        }

        // If it is bedtime, find or dig burrow.
        Location currL = w.getCurrentLocation();
        Home home = getHome();
        // If rabbit is homeless, try to dig a burrow.
        if (getHome() == null) {
            try {
                findHome(w, "RabbitBurrow");
            } catch (IllegalStateException e) {
                try {
                    digBurrow(w, new RabbitBurrow());
                } catch (IllegalStateException i) { tryRandomMove(w); }
            }
                } // go home and hide
        else if (currL.equals(w.getLocation(home))) {
                    hide(w);
                } else {
                    moveToLocation(w,w.getLocation(getHome()));
                }

            }

            public void tryToEat (World w){
                Location l = w.getCurrentLocation();
                if (!w.containsNonBlocking(l)) {
                    return;
                }
                Object nonBlocking = w.getNonBlocking(l);
                if (getDiet().contains(nonBlocking.getClass().getSimpleName())) {
                    Edible edible = (Edible) nonBlocking;
                    eat(w, edible);
                }
            }
        }