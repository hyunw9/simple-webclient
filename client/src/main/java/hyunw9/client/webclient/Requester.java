package hyunw9.client.webclient;

import static hyunw9.client.util.JsonUtil.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.springframework.web.reactive.function.client.WebClient;

import hyunw9.client.core.HttpMethod;
import hyunw9.client.core.RequestFilter;
import hyunw9.client.core.RequestSpec;
import hyunw9.client.core.RetryOption;
import hyunw9.client.core.TimeoutOption;
import hyunw9.client.core.filter.RetryFilter;
import hyunw9.client.core.filter.TimeoutFilter;
import hyunw9.client.util.JsonUtil;

public final class Requester {

	/* ===== factory ===== */
	public static RequestBuilder request(String url) {
		return new RequestBuilder(url);
	}

	/* ===== builder ===== */
	public static final class RequestBuilder {
		private final RequestSpec.Builder spec;
		private final List<RequestFilter> filters = new ArrayList<>();

		private RequestBuilder(String url) {
			this.spec = RequestSpec.to(url);
			spec.header("Content-Type", "application/json");
		}

		public RequestBuilder method(HttpMethod m) {
			spec.method(m);
			return this;
		}

		public RequestBuilder header(String k, String v) {
			spec.header(k, v);
			return this;
		}

		public RequestBuilder param(String k, Object v) {
			spec.param(k, v);
			return this;
		}

		public RequestBuilder body(Object o) {
			spec.jsonBody(JsonUtil.toJson(o));
			return this;
		}

		public RequestBuilder retry(RetryOption o) {
			filters.add(new RetryFilter(o));
			return this;
		}

		public RequestBuilder timeout(TimeoutOption o) {
			filters.add(new TimeoutFilter(o.duration));
			return this;
		}

		public RequestBuilder timeout(Duration d) {
			filters.add(new TimeoutFilter(d));
			return this;
		}

		/* 동기 */
		public <T> T send(Class<T> type) {
			return core().call(spec.build(), type);
		}

		/* 비동기 */
		public <T> CompletableFuture<T> async(Class<T> type) {
			return core().callAsync(spec.build(), type);
		}

		private RequesterCore core() {
			RequestFilter chain = (s, n) -> n.proceed(s);
			for (RequestFilter f : filters) {
				final RequestFilter finalChain = chain;
				chain = (spec, n) -> f.apply(spec, s -> finalChain.apply(s, n));
			}
			return new RequesterCore(chain);
		}
	}

	/* ===== internal core ===== */
	private static final class RequesterCore {
		private final WebClient client = WebClient.builder().build();
		private final RequestFilter chain;

		RequesterCore(RequestFilter c) {
			this.chain = c;
		}

		<T> T call(RequestSpec s, Class<T> t) {
			return callAsync(s, t).join();
		}

		<T> CompletableFuture<T> callAsync(RequestSpec requestSpec, Class<T> type) {
			return chain.apply(requestSpec, this::invoke)
				.thenCompose(response -> JsonUtil.parseIfJson(response, type))
				// .thenApply(r -> JsonUtil.fromJson(r.rawBody(), type))
				.toCompletableFuture();
		}

		private CompletionStage<RequestFilter.Response> invoke(RequestSpec s) {
			return client.method(org.springframework.http.HttpMethod.valueOf(s.method().name()))
				.uri(s.uri())
				.headers(h -> h.setAll(s.headers()))
				.bodyValue(s.jsonBody())
				.retrieve()
				.toEntity(String.class)
				.map(e -> new RequestFilter.Response(
					e.getStatusCode().value(),
					e.getBody(),
					e.getHeaders().toSingleValueMap()))
				.toFuture();
		}
	}
}
