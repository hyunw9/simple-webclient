package hyunw9.client.core;

import java.time.Duration;

public enum RetryOption {

	NO_RETRY(0, Duration.ZERO, 1.0),
	RETRY_3_EXP_BACKOFF(3, Duration.ofMillis(200), 2.0),
	RETRY_5_EXP_BACKOFF(5, Duration.ofMillis(200), 2.0),
	RETRY_3_FIXED_300MS(3, Duration.ofMillis(300), 1.0),
	CUSTOM(-1, Duration.ZERO, 1.0);

	public final int attempts;
	public final Duration base;
	public final double factor;

	RetryOption(int a, Duration b, double f) {
		attempts = a;
		base = b;
		factor = f;
	}
}
