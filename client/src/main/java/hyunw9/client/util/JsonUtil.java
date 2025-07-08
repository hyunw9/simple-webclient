package hyunw9.client.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import hyunw9.client.core.RequestFilter;

public final class JsonUtil {
	private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(
			new JavaTimeModule())
		.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

	public static String toJson(Object object) {
		try {
			return MAPPER.writeValueAsString(object);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromJson(String json, Class<T> type) {
		try {
			return MAPPER.readValue(json, type);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T fromJson(String json, TypeReference<T> reference) {
		try {
			return MAPPER.readValue(json, reference);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> CompletionStage<T> parseIfJson(RequestFilter.Response response, Class<T> type) {

		/* 0) String.class 이면 JSON 검사 건너뛰고 그대로 반환 */
		if (String.class.equals(type)) {
			@SuppressWarnings("unchecked")
			T casted = (T)response.rawBody();
			return CompletableFuture.completedFuture(casted);
		}

		// 1) JSON 가능성 간단 판별
		final String contentType = response.headers().getOrDefault("Content-Type", "");
		final String body = response.rawBody().trim();

		final boolean maybeJson = contentType.contains("json")            // application/json, */*+json …
			|| body.startsWith("{")            // object
			|| body.startsWith("[");           // array

		if (!maybeJson || body.isEmpty()) {
			return CompletableFuture.failedStage(
				new IllegalStateException("Not-JSON (ct=" + contentType + "): " + body));
		}

		// 2) Jackson 파싱 시도 + 예외 래핑
		try {
			final T parsed = JsonUtil.fromJson(body, type);
			return CompletableFuture.completedFuture(parsed);
		} catch (Exception ex) {
			return CompletableFuture.failedStage(
				new IllegalStateException("JSON parse error: " + ex.getMessage(), ex));
		}
	}
}
