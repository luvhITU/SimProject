import java.util.Set;

public class Config {

    private Config() {
    }

    public static class Grass {
        public static final int NUTRITION = 20;
    }

    public static class BerryBush {
        public static  final int NUTRITION = 20;
    }

    public static class Rabbit {
        public static final Set<String> DIET = Set.of("Grass");
        public static final int NUTRITION = 80;
        public static final int DAMAGE = 1;
        public static final double ABSORPTION_PERCENTAGE = 1.0;
    }

    public static class Wolf {
        public static final Set<String> DIET = Set.of("Rabbit");
        public static final int NUTRITION = 150;
        public static final int DAMAGE = 40;
        public static final double ABSORPTION_PERCENTAGE = 0.8;
    }

    public static class Bear {
        public static final Set<String> DIET = Set.of("Berry", "Rabbit", "Wolf");
        public static final int NUTRITION = 300;
        public static final int DAMAGE = 100;
        public static final double ABSORPTION_PERCENTAGE = 0.5;
    }
}
