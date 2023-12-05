import itumulator.simulator.Actor;
import itumulator.world.World;

public class Rabbit extends Animal implements Actor {

    private static final int FIND_HOME_THRESHOLD = 8;

    public Rabbit() {
        super(Config.Rabbit.DIET, Config.Rabbit.DAMAGE, Config.Rabbit.HEALTH, Config.Rabbit.AGGRESSION, Config.Rabbit.SPEED);
    }

    @Override
    public void act(World w) {
        if (getIsDead()) { return; }
        reduceReproductionCooldown();
        if (getHome() == null && (w.getCurrentTime() == 9 || getEnergy() < 20)) {
            findOrCreateBurrow(w, "RabbitBurrow");
        }
        if (!getIsAwake() && w.getCurrentTime() == 0) {
            emerge(w);
        }
        if (getIsAwake()) {
            setTilesInSight(w.getSurroundingTiles(5));
        }


        super.act(w);

//        if (getIsAwake()) {
//            doMovementPackage(w);
//            if (!getHasMatedToday() && w.getCurrentTime() > 0) {
//                tryToMate(w);
//            }
//            tryToEat(w);
//        }

    }

//    @Override
//    public DisplayInformation getInformation() {
//        return new DisplayInformation(Color.blue, "rabbit-large");
//    }

    private void findOrCreateBurrow(World w, String type) {
        try {
            System.out.println("Finding Home");
            findHome(w, type);
        } catch (IllegalStateException e1) {
            try {
                System.out.println("Didn't find home, digging home.");
                digBurrow(w, new RabbitBurrow());
            } catch (IllegalStateException e2) {randomMove(w);}
        }
    }

//            public void tryToEat (World w){
//                Location l = w.getCurrentLocation();
//                if (!w.containsNonBlocking(l)) {
//                    return;
//                }
//                Object nonBlocking = w.getNonBlocking(l);
//                if (getDiet().contains(nonBlocking.getClass().getSimpleName())) {
//                    Edible edible = (Edible) nonBlocking;
//                    eat(w, edible);
//                }
//            }
//        }

    @Override
    public void sleep(World w) {
        hide(w);
    }
}