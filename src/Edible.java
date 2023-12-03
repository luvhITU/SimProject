public abstract class Edible extends SimComponent {
    private int nutrition;

    public Edible(int nutrition) {
        this.nutrition = nutrition;
    }

    public int getNutrition() {
        return nutrition;
    }

    public void setNutrition(int nutrition) {
        this.nutrition = Math.max(0, Math.min(this.nutrition, nutrition));
    }
}
