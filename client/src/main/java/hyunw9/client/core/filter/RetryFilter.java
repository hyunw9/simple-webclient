package hyunw9.client.core.filter;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import hyunw9.client.core.RequestFilter;
import hyunw9.client.core.RequestSpec;
import hyunw9.client.core.RetryOption;

public class RetryFilter implements RequestFilter {

	private final int attempts;
	private final Duration base;
	private final double factor;

	public RetryFilter(RetryOption o) {
		attempts = o.attempts;
		base = o.base;
		factor = o.factor;
	}

	@Override
	public CompletionStage<Response> apply(RequestSpec s, Chain n) {
		var p = new CompletableFuture<Response>();
		attempt(s, n, 1, p, base);
		return p;
	}

	private void attempt(RequestSpec s, Chain n, int i,
		CompletableFuture<Response> p, Duration d) {
		n.proceed(s).whenComplete((r, e) -> {
			if (e == null) {
				p.complete(r);
			} else if (i < attempts) {
				var next = d.multipliedBy((long)factor);
				CompletableFuture.delayedExecutor(d.toMillis(), TimeUnit.MILLISECONDS)
					.execute(() -> attempt(s, n, i + 1, p, next));
			} else {
				p.completeExceptionally(e);
			}
		});
	}
}
