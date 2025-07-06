package client.common;

public enum RetryPolicy {
    NO_RETRY(0),
    ONE_TIME(1),
    THREE_TIMES(3),
    FIVE_TIMES(5);

    public static RetryPolicy valueOfOrDefault(int attempts) {
        for (RetryPolicy policy : values()) {
            if (policy.getAttempts() == attempts) {return policy;}
        }
        return NO_RETRY; // Default
    }
    private final int attempts;

    RetryPolicy(int attempts) {
        this.attempts = attempts;
    }

    public int getAttempts() {
        return attempts;
    }
}
