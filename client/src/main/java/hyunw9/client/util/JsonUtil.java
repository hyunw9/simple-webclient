package hyunw9.client.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import hyunw9.client.core.RequestFilter;

public final class JsonUtil {
	private static final ObjectMapper MAPPER = new ObjectMapper()
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	public static String toJson(Object o) {
		try {
			return MAPPER.writeValueAsString(o);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromJson(String j, Class<T> t) {
		try {
			return MAPPER.readValue(j, t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> CompletionStage<T> parseIfJson(
		RequestFilter.Response r,
		Class<T> type) {

		/* 0) String.class 이면 JSON 검사 건너뛰고 그대로 반환 */
		if (String.class.equals(type)) {
			@SuppressWarnings("unchecked")
			T casted = (T) r.rawBody();
			return CompletableFuture.completedFuture(casted);
		}

		/* 1) JSON 가능성 간단 판별 */
		final String contentType   = r.headers().getOrDefault("Content-Type", "");
		final String body = r.rawBody().trim();

		final boolean maybeJson = contentType.contains("json")            // application/json, */*+json …
			|| body.startsWith("{")            // object
			|| body.startsWith("[");           // array

		if (!maybeJson || body.isEmpty()) {
			return CompletableFuture.failedStage(
				new IllegalStateException("Not-JSON (ct=" + contentType + "): " + body));
		}

		/* 2) Jackson 파싱 시도 + 예외 래핑 */
		try {
			final T parsed = JsonUtil.fromJson(body, type);
			return CompletableFuture.completedFuture(parsed);
		} catch (Exception ex) {
			return CompletableFuture.failedStage(
				new IllegalStateException("JSON parse error: " + ex.getMessage(), ex));
		}
	}
}
