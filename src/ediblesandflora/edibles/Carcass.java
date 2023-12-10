package ediblesandflora.edibles;

import animals.Animal;
import ediblesandflora.Fungus;
import utils.HelperMethods;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.Color;

public class Carcass extends Edible implements DynamicDisplayInformationProvider {
    private static final int defaultNutrition = 30;
    private static final double fungusThresholdPercentage = 0.3;
//    private final int initialNutrition;
    private int fungusGrowth = 0;
    private final int fungusThreshold;
    private boolean isInfected = false;
    private boolean fungusCreated;
    private Fungus fungus = null;

    public Carcass(Animal animal, boolean isInfected) {
        super(animal.getMaxHealth() / 2, 0);
        this.isInfected = isInfected;
        this.fungusThreshold = (int) (fungusThresholdPercentage * maxNutrition);
    }

    public Carcass(boolean isInfected) {
        super(defaultNutrition, 0);
        this.isInfected = isInfected;
        this.fungusThreshold = (int) (fungusThresholdPercentage * maxNutrition);
    }

    @Override
    public void act(World w) {
        super.act(w);
        degrade(w);
        checkForInfection(w);
    }

    @Override
    public DisplayInformation getInformation() {
        String imageKey = getNutrition() > 100 ? "carcass" : "carcass-small";
        if (isInfected) {
            imageKey += "-fungus";
        }
        return new DisplayInformation(Color.magenta, imageKey);
    }

    public boolean getIsInfected() {
        return isInfected;
    }

    private void degrade(World w) {
        reduceNutritionBy(1);
        if (getNutrition() <= 0) {
            Location deathLocation = w.getLocation(this);
            deleteAndSpawnFungus(w, deathLocation);
        }
    }

    private void checkForInfection(World w) {
        double infectionProbability = (maxNutrition - getNutrition()) / 100.0;
        if (!isInfected && HelperMethods.getRandom().nextDouble() < infectionProbability) {
            //System.out.println("ediblesandflora.edibles.Carcass is infected!");
            isInfected = true;
            fungusGrowth = 0;
            fungusCreated = false;
        }
        if (isInfected) {
            growFungus(w);
        }
    }

    private void growFungus(World w) {
        if (!fungusCreated && getNutrition() % 2 == 0) {
            fungusGrowth++;
            if (fungusGrowth >= fungusThreshold) {
                createFungus(w);
                fungusCreated = true;
            }
        }
    }

    private void createFungus(World w) {
        fungus = new Fungus(maxNutrition);
    }

    private void deleteAndSpawnFungus(World w, Location deathLocation) {
        delete(w);
        if (fungusCreated) {
            w.setTile(deathLocation, fungus);
        }
    }

    public void setInfected() {
        isInfected = true;
    }
}
