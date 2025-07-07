package hyunw9.client.core;

import java.time.Duration;

public enum TimeoutOption {
    TIMEOUT_1_SEC(Duration.ofSeconds(1)),
    TIMEOUT_2_SEC(Duration.ofSeconds(2)),
    TIMEOUT_5_SEC(Duration.ofSeconds(5)),
    CUSTOM(Duration.ZERO);
    public final Duration duration;

    TimeoutOption(Duration d) {duration = d;}
}
