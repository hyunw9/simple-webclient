package hyunw9.client.webclient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.core.type.TypeReference;

import hyunw9.client.core.HttpMethod;
import hyunw9.client.core.RequestFilter;
import hyunw9.client.core.RequestSpec;
import hyunw9.client.core.RetryOption;
import hyunw9.client.core.TimeoutOption;
import hyunw9.client.core.filter.FilterBuilder;
import hyunw9.client.core.filter.RetryFilter;
import hyunw9.client.core.filter.TimeoutFilter;
import hyunw9.client.transport.HttpTransport;
import hyunw9.client.transport.WebClientTransport;
import hyunw9.client.util.JsonUtil;

public final class Requester {

	private final RequesterCore requesterCore;

	public Requester(RequesterCore requesterCore) {
		this.requesterCore = requesterCore;
	}

	public static RequestBuilder request(String url) {
		return new RequestBuilder(url);
	}

	public static final class RequestBuilder {
		private final RequestSpec.Builder spec;
		private final List<RequestFilter> filters = new ArrayList<>();

		private RequestBuilder(String url) {
			this.spec = RequestSpec.to(url);
			spec.header("Content-Type", "application/json");
		}

		public RequestBuilder method(HttpMethod method) {
			spec.method(method);
			return this;
		}

		public RequestBuilder header(String key, String value) {
			spec.header(key, value);
			return this;
		}

		public RequestBuilder param(String key, Object value) {
			spec.param(key, value);
			return this;
		}

		public RequestBuilder body(Object object) {
			spec.jsonBody(JsonUtil.toJson(object));
			return this;
		}

		public RequestBuilder retry(RetryOption object) {
			filters.add(new RetryFilter(object));
			return this;
		}

		public RequestBuilder timeout(TimeoutOption option) {
			filters.add(new TimeoutFilter(option.duration));
			return this;
		}

		public RequestBuilder timeout(Duration duration) {
			filters.add(new TimeoutFilter(duration));
			return this;
		}

		/* 동기 */
		public <T> T send(Class<T> type) {
			return core().call(spec.build(), type);
		}

		public <T> T send(TypeReference<T> reference) {
			return core().call(spec.build(), reference);
		}

		/* 비동기 */
		public <T> CompletableFuture<T> async(Class<T> type) {
			return core().callAsync(spec.build(), type);
		}

		private RequesterCore core() {
			HttpTransport transport = new WebClientTransport();
			RequestFilter pipeline = FilterBuilder.build(filters, transport);
			return new RequesterCore(pipeline);
		}
	}

	/* ===== internal core ===== */
	// private static final class RequesterCore {
	// 	private final WebClient client = WebClient.builder().build();
	// 	private final RequestFilter chain;
	//
	// 	RequesterCore(RequestFilter chain) {
	// 		this.chain = chain;
	// 	}
	//
	// 	<T> T call(RequestSpec spec, Class<T> type) {
	// 		return callAsync(spec, type).join();
	// 	}
	//
	// 	<T> T call(RequestSpec spec, TypeReference<T> reference) {
	// 		return callAsync(spec, reference).join();
	// 	}
	//
	// 	<T> CompletableFuture<T> callAsync(RequestSpec requestSpec, Class<T> type) {
	// 		return chain.apply(requestSpec, tr::invoke)
	// 			.thenCompose(response -> JsonUtil.parseIfJson(response, type))
	// 			.toCompletableFuture();
	// 	}
	//
	// 	<T> CompletableFuture<T> callAsync(RequestSpec spec,  TypeReference<T> reference) {
	// 		return chain.apply(spec, this::invoke)
	// 			.thenApply(response -> JsonUtil.fromJson(response.rawBody(), reference))
	// 			.toCompletableFuture();
	// 	}
	// }
}
