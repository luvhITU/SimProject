package utils;

import java.util.Set;

public class Config {
    private Config() {
    }

    /***
     * Has: int "NUTRITION"
     */
    public static class Grass {
        public static final int NUTRITION = 5;
    }

    /***
     * Has: Set<String> "DIET", int "DAMAGE", int "HEALTH", int "SPEED" and int "MATING_COOLDOWN_DAYS"
     */
    public static class Rabbit {
        public static final Set<String> DIET = Set.of("Grass");
        public static final int DAMAGE = 0;
        public static final int HEALTH = 30;
        public static final int SPEED = 1;
        public static final int MATING_COOLDOWN_DAYS = 2;
        public static final int MAX_BURROW_OCCUPANTS = 5;
    }

    /***
     * Has: Set<String> "DIET", int "DAMAGE", int "HEALTH", int "SPEED" and int "MATING_COOLDOWN_DAYS"
     */
    public static class Wolf {
        public static final Set<String> DIET = Set.of("Rabbit", "Bear", "Wolf", "Carcass");
        public static final int DAMAGE = 30;
        public static final int HEALTH = 100;
        public static final int SPEED = 2;
        public static final int MATING_COOLDOWN_DAYS = 3;
        public static final int MAX_PACK_SIZE = 5;
    }

    /***
     * Has: Set<String> "DIET", int "DAMAGE", int "HEALTH", int "SPEED" and int "MATING_COOLDOWN_DAYS"
     */
    public static class Bear {
        public static final Set<String> DIET = Set.of("BerryBush", "Rabbit", "Wolf", "Bear", "Carcass");
        public static final int DAMAGE = 100;
        public static final int HEALTH = 300;
        public static final int SPEED = 2;
        public static final int MATING_COOLDOWN_DAYS = 0;
    }

    public static class Fox {
        public static final Set<String> DIET = Set.of("BerryBush", "Rabbit", "Fox", "Carcass");
        public static final int DAMAGE = 15;
        public static final int HEALTH = 60;
        public static final int SPEED = 2;
        public static final int MATING_COOLDOWN_DAYS = 3;
        public static final int MAX_PACK_SIZE = 6;
    }

    /***
     * Has: int "NUTRITION" and int "renewTimeDays"
     */
    public static class BerryBush {
        public static final int NUTRITION = 30;
        public static final int renewTimeDays = 60;
    }
}