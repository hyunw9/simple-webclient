package hyunw9.client.transport;

import java.util.concurrent.CompletionStage;

import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import hyunw9.client.core.RequestFilter;
import hyunw9.client.core.RequestSpec;

public class WebClientTransport implements HttpTransport{

	private final WebClient client = WebClient.builder().build();
	public CompletionStage<RequestFilter.Response> send(RequestSpec spec) {
		return client.method(HttpMethod.valueOf(spec.method().name()))
			.uri(spec.uri())
			.headers(h -> h.setAll(spec.headers()))
			.bodyValue(spec.jsonBody())
			.retrieve()
			.toEntity(String.class)
			.map(e -> new RequestFilter.Response(
				e.getStatusCode().value(),
				e.getBody(),
				e.getHeaders().toSingleValueMap()))
			.toFuture();
	}
}
