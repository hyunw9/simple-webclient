package client.common;

public enum TimeoutPolicy {
    VERY_SHORT(3),
    SHORT(5),
    MEDIUM(10),
    LONG(30),
    VERY_LONG(60);

    public static TimeoutPolicy valueOfOrDefault(int seconds) {
        for (TimeoutPolicy policy : values()) {
            if (policy.getSeconds() == seconds) {return policy;}
        }
        return MEDIUM; // Default
    }
    private final int seconds;

    TimeoutPolicy(int seconds) {
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }
}
