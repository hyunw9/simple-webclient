package hyunw9.client.core.filter;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import hyunw9.client.core.Chain;
import hyunw9.client.core.RequestFilter;
import hyunw9.client.core.RequestSpec;
import hyunw9.client.core.RetryOption;

public class RetryFilter implements RequestFilter {

	private final int attempts;
	private final Duration base;
	private final double factor;

	public RetryFilter(RetryOption option) {
		attempts = option.attempts;
		base = option.base;
		factor = option.factor;
	}

	@Override
	public CompletionStage<Response> apply(RequestSpec spec, Chain chain) {
		var p = new CompletableFuture<Response>();
		attempt(spec, chain, 1, p, base);
		return p;
	}

	private void attempt(RequestSpec spec, Chain chain, int i,
		CompletableFuture<Response> responseFuture, Duration duration) {
		chain.proceed(spec).whenComplete((r, e) -> {
			if (e == null) {
				responseFuture.complete(r);
			} else if (i < attempts) {
				var next = duration.multipliedBy((long)factor);
				CompletableFuture.delayedExecutor(duration.toMillis(), TimeUnit.MILLISECONDS)
					.execute(() -> attempt(spec, chain, i + 1, responseFuture, next));
			} else {
				responseFuture.completeExceptionally(e);
			}
		});
	}
}
