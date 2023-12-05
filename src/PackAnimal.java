import itumulator.world.Location;
import itumulator.world.NonBlocking;
import itumulator.world.World;

import java.util.HashSet;
import java.util.Set;

public class PackAnimal {

    @Override
    public int calcNutritionAbsorbed(int nutrition) {
        return (int) Math.round( (nutrition / (maxHealth / 100.0)) / packList.size() );
    }

    public void createBurrow(World w) {
        this.wolfburrow = new WolfBurrow(); //TODO: make it so doesn't delete other burrows
        if(w.containsNonBlocking(homeLocation)) {
            if ((w.getTile(homeLocation) instanceof Home)) {
                homeLocation = HelperMethods.getClosestEmptyTile(w, homeLocation, 5);
            } else { w.delete(w.getNonBlocking(homeLocation)); }
        }
        w.setTile(homeLocation, wolfburrow);
    }

    private Animal findClosestPartner(World w) {
        Set<Location> partnerLocations = new HashSet<>();
        for (Location n : tilesInSight) {
            Object entity = w.getTile(n);
            if (entity == null || !getClass().equals(entity.getClass())) {
                continue;
            }
            Animal potentialPartner = (Animal) entity;
            if (potentialPartner.isHorny(w)) {
                partnerLocations.add(n);
            }
        }
        if (partnerLocations.isEmpty()) {
            return null;
        }
        Location partnerLocation = HelperMethods.findNearestLocationByType(w, w.getCurrentLocation(), partnerLocations, getClass().getSimpleName());
        System.out.println(this + " | " + w.getTile(partnerLocation));
        return (Animal) w.getTile(partnerLocation);
    }

    private String getMode(World w) {
        if (!isAwake) {
            return "stay";
        }
        boolean isPredatorInSight = isPredatorInSight(w);
        if (isPredatorInSight) {System.out.println("Predator " + findClosestPredator(w));}
        boolean isHungry = satiation < 50;
        int activeAggression = (isHungry && aggression == 2) ? 3 : aggression;
        boolean isTired = isTired(w);
        Animal closestPartner = null;
        boolean hasLegalMove = !w.getEmptySurroundingTiles().isEmpty();
        if (isHorny(w)) {
            closestPartner = findClosestPartner(w);
        }

        String mode = "flee";

        if (isPredatorInSight) {
            if (activeAggression == 3 || activeAggression == 2 && !isLargerPredatorInSight(w)) {
                mode = "attack";
            }
        } else if (!hasLegalMove) {
            mode = "stay";
        } else if (home != null && isTired) {
            mode = "sleep";
        } else if (closestPartner != null) {
            mode = "reproduce";
        } else if (satiation < 80 && findClosestEdible(w) != null) {
            Edible edible = findClosestEdible(w);
            if (edible instanceof Animal && !((Animal) edible).isDead()) {
                mode = "attack";
            } else { mode = "eat"; }
        } else {
            mode = "random move";
        }
        return mode;
    }

    private void aiPackage(World w) {
        String mode = getMode(w);
        Location targetLocation = null;
        switch (mode) {
            case "stay": {
                System.out.println("STAY");
                return;
            }
            case "flee": {
                System.out.println("FLEE");
                Location predatorL = w.getEntities().get(findClosestPredator(w));
                flee(w, predatorL);
                return;
            }
            // TODO Man skal kunne flytte sig mod prey og attacke i samme step.
            case "attack": {
                System.out.println("ATTACK");
                // find largest predator or non predator
                // if it is within 1 block radius, attack. else move to it
                Animal target = findLargestPredator(w);
                if (target == null) {
                    target = (Animal) findClosestEdible(w);
                }
                System.out.println(target);
                targetLocation = getLocation(w, target);
                if (w.getSurroundingTiles().contains(targetLocation)) {
                    attack(w, target);
                } else {
                    hunt(w, targetLocation);
                }
                return;
            }
            case "sleep": {
                System.out.println("SLEEP");
                targetLocation = getLocation(w, home);
                if (w.getCurrentLocation().equals(targetLocation)) {
                    sleep(w);
                    return;
                }
                break;
            }
            case "reproduce": {
                System.out.println("REPRODUCE");
                Animal partner = findClosestPartner(w);
                targetLocation = getLocation(w, partner);
                if (w.getSurroundingTiles().contains(targetLocation) && !w.getEmptySurroundingTiles().isEmpty()) {
                    reproduce(w, partner);
                    return;
                }
                break;
            }
            case "eat": {
                System.out.println("EAT");
                Edible edible = findClosestEdible(w);
                System.out.println(this + " " + edible);
                targetLocation = getLocation(w, edible);
                boolean isNonBlocking = edible instanceof NonBlocking;
                if (isNonBlocking && w.getCurrentLocation().equals(targetLocation)
                        || w.getSurroundingTiles().contains(targetLocation)) {
                    System.out.println(satiation);
                    eat(w, edible);
                    System.out.println(satiation);
                    return;
                }
                break;
            }

            case "random move": {
                randomMove(w);
                return;
            }
        }
        moveTo(w, targetLocation);
    }
}
