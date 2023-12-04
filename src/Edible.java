import static java.lang.Math.max;

public abstract class Edible extends SimComponent {
    private int nutrition;


    public Edible(int nutrition) {
        this.nutrition = nutrition;
    }

    public int getNutrition() {
        return nutrition;
    }

    public void setNutrition(int nutrition) {
        this.nutrition = max(0, nutrition);
    }
}
