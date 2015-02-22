package ca.dalezak.android.base.utils;

public class UUID {

    /**
     * Get a random UUID with dashes
     * @return uuid
     */
    public static String getRandom() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * Get a random UUID with NO dashes
     * @return uuid
     */
    public static String getRandomNoDashes() {
        String uuid = java.util.UUID.randomUUID().toString();
        return uuid.replace("-", "");
    }
}
