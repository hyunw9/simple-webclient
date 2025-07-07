package hyunw9.client.core;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public record RequestSpec(HttpMethod method,
						  URI uri,
						  Map<String, String> headers,
						  String jsonBody) {

	public static Builder to(String url) {
		return new Builder(url);
	}

	public static final class Builder {
		private final String url;
		private final Map<String, String> headers = new LinkedHashMap<>();
		private final Map<String, Object> params = new LinkedHashMap<>();
		private HttpMethod method = HttpMethod.GET;
		private String jsonBody = "";

		public Builder(String url) {
			this.url = url;
		}

		private static String encode(String s) {
			return URLEncoder.encode(s, StandardCharsets.UTF_8);
		}

		public Builder method(HttpMethod m) {
			method = m;
			return this;
		}

		public Builder header(String k, String v) {
			headers.put(k, v);
			return this;
		}

		public Builder param(String k, Object v) {
			params.put(k, v);
			return this;
		}

		public Builder jsonBody(String json) {
			jsonBody = json;
			return this;
		}

		public RequestSpec build() {
			StringBuilder sb = new StringBuilder(url);
			if (!params.isEmpty()) {
				sb.append("?");
				params.forEach((k, v) -> sb.append(encode(k)).append("=")
					.append(encode(String.valueOf(v))).append("&"));
				sb.setLength(sb.length() - 1);
			}
			return new RequestSpec(method, URI.create(sb.toString()),
				Map.copyOf(headers), jsonBody);
		}
	}
}
