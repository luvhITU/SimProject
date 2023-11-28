public class Edible extends SimComponent {
    private int nutrition;

    public Edible(int nutrition) {
        this.nutrition = nutrition;
    }

    public int getNutrition() {
        return nutrition;
    }
}
