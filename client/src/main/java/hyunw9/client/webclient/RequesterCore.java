package hyunw9.client.webclient;

import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.type.TypeReference;

import hyunw9.client.core.RequestFilter;
import hyunw9.client.core.RequestSpec;
import hyunw9.client.util.JsonUtil;

public final class RequesterCore {

	private final RequestFilter pipeline;

	public RequesterCore(RequestFilter pipeline) {
		this.pipeline = pipeline;
	}

	<T> T call(RequestSpec spec, Class<T> type) {
		return callAsync(spec, type).join();
	}

	<T> T call(RequestSpec spec, TypeReference<T> reference) {
		return callAsync(spec, reference).join();
	}

	<T> CompletableFuture<T> callAsync(RequestSpec requestSpec, Class<T> type) {
		return pipeline.apply(requestSpec, (__) -> null)
			.thenCompose(response -> JsonUtil.parseIfJson(response, type))
			.toCompletableFuture();
	}

	<T> CompletableFuture<T> callAsync(RequestSpec spec, TypeReference<T> reference) {
		return pipeline.apply(spec, (__) -> null)
			.thenApply(response -> JsonUtil.fromJson(response.rawBody(), reference))
			.toCompletableFuture();
	}
}
