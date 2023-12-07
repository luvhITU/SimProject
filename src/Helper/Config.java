package Helper;

import java.util.Set;

public class Config {
    private Config() {
    }

    public static class Grass {
        public static final int NUTRITION = 20;
    }

    public static class Rabbit {
        public static final Set<String> DIET = Set.of("Grass");
        public static final int DAMAGE = 0;
        public static final int HEALTH = 30;
        public static final int SPEED = 1;
        public static final int MATING_COOLDOWN_DAYS = 2;
    }

    public static class Wolf {
        public static final Set<String> DIET = Set.of("Rabbit", "Bear", "Wolf", "Meat");
        public static final int DAMAGE = 20;
        public static final int HEALTH = 100;
        public static final int SPEED = 2;
        public static final int MATING_COOLDOWN_DAYS = 0;
    }

    public static class Bear {
        public static final Set<String> DIET = Set.of("Berry", "Rabbit", "Wolf", "Bear", "Meat");
        public static final int DAMAGE = 100;
        public static final int HEALTH = 300;
        public static final int SPEED = 2;
        public static final int MATING_COOLDOWN_DAYS = 0;
    }

    public static class BerryBush {
        public static final int NUTRITION = 30;
        public static final int renewTimeDays = 60;
    }
}