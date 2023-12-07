package MapComponents;

import Abstracts.Animal;
import Abstracts.Edible;
import Helper.HelperMethods;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.World;

import java.awt.Color;

public class Carcass extends Edible implements DynamicDisplayInformationProvider {
    private static final int defaultNutrition = 30;
    private static final double fungusThresholdPercentage = 0.3;
    private final int initialNutrition;
    private int fungusGrowth = 0;
    private final int fungusThreshold;
    private final String carcassType;
    private boolean isInfected = false;
    private boolean fungusCreated;
    private Fungus fungus = null;

    public Carcass(Animal animal, boolean isInfected) {
        super(animal.maxHealth);
        this.carcassType = animal.getType();
        this.isInfected = isInfected;
        this.initialNutrition = getNutrition();
        this.fungusThreshold = (int) (fungusThresholdPercentage * initialNutrition);
    }

    public Carcass(boolean isInfected) {
        super(defaultNutrition);
        this.carcassType = null;
        this.isInfected = isInfected;
        this.initialNutrition = getNutrition();
        this.fungusThreshold = (int) (fungusThresholdPercentage * initialNutrition);
    }

    @Override
    public void act(World w) {
        //System.out.println("Nutrition: " + getNutrition());
        //System.out.println("MapComponents.Fungus Growth: " + fungusGrowth);
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

    @Override
    public String getType() {
        return carcassType;
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
        double infectionProbability = (initialNutrition - getNutrition()) / 100.0;
        if (!isInfected && HelperMethods.getRandom().nextDouble() < infectionProbability) {
            //System.out.println("MapComponents.Carcass is infected!");
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
        //System.out.println("MapComponents.Fungus has grown inside the carcass!");
        fungus = new Fungus(initialNutrition);
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
