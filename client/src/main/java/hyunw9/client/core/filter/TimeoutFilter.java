package hyunw9.client.core.filter;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import hyunw9.client.core.RequestFilter;
import hyunw9.client.core.RequestSpec;

public class TimeoutFilter implements RequestFilter {

	private final Duration timeout;

	public TimeoutFilter(Duration t) {
		timeout = t;
	}

	@Override
	public CompletionStage<Response> apply(RequestSpec s, Chain n) {
		return n.proceed(s).toCompletableFuture().orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS);
	}
}
