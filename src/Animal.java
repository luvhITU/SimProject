import itumulator.simulator.Actor;

abstract class Animal implements Actor, Eatable {
    protected boolean isAlice;
    protected int satiation;
    protected Eatable[] foodSources;
    protected int age;
/*
    public Animal(Eatable[] foodSources) {
        isAlive = true;
        satiation = 10;
        this.foodSources = foodSources;
        age = 0;
    }

 */
}

