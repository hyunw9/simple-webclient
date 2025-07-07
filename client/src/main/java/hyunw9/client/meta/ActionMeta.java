package hyunw9.client.meta;

import java.util.Map;

import hyunw9.client.core.HttpMethod;

public record ActionMeta(HttpMethod method,
						 String pathTemplate,
						 Map<String, String> headers,
						 Map<String, Object> defaultParams) {
}
