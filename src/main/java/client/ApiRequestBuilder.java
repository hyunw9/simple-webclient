//package client;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import client.common.ApiResponse;
//import client.common.ApiResponse.CommonErrorResponse;
//import client.common.RetryPolicy;
//import client.common.TimeoutPolicy;
////import client.config.WebClientPool;
//import client.exception.ApiClientException;
//import io.netty.handler.codec.http.HttpMethod;
//import reactor.core.publisher.Mono;
//
///**
// *  API 요청을 구성하는 빌더 클래스.
// * 메서드 체이닝을 통해 유연하게 요청을 생성하고 실행합니다.
// */
//public class ApiRequestBuilder {
//    private static final Logger logger = LoggerFactory.getLogger(ApiRequestBuilder.class);
//    private static final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱/생성을 위한 ObjectMapper
//
//    private final WebClient webClient;
//    private final HttpMethod method;
//    private String uri; // 가변적으로 변경될 수 있도록 String으로 선언
//
//    private Object body;
//    private Map<String, String> headers = new HashMap<>();
//    private Map<String, String> queryParams = new HashMap<>();
//    private RetryPolicy retryPolicy;
//    private TimeoutPolicy timeoutPolicy;
//
//    // ApiClient에서만 생성 가능하도록 private
//    ApiRequestBuilder(WebClient webClient, HttpMethod method, String uriTemplate, RetryPolicy defaultRetryPolicy, TimeoutPolicy defaultTimeoutPolicy) {
//        this.webClient = webClient;
//        this.method = method;
//        this.uriTemplate = uriTemplate;
//        this.currentRetryPolicy = defaultRetryPolicy; // ServiceConfig에서 온 기본값
//        this.currentTimeoutPolicy = defaultTimeoutPolicy; // ServiceConfig에서 온 기본값
//    }
//
//    /**
//     * 요청 바디를 설정합니다. POST, PUT 요청 시 사용됩니다.
//     * @param body 요청에 포함될 객체 (JSON으로 자동 변환)
//     * @return 빌더 인스턴스
//     */
//    public ApiRequestBuilder body(Object body) {
//        this.body = body;
//        return this;
//    }
//
//    /**
//     * 요청 헤더를 추가합니다.
//     * @param key 헤더 이름
//     * @param value 헤더 값
//     * @return 빌더 인스턴스
//     */
//    public ApiRequestBuilder header(String key, String value) {
//        this.headers.put(key, value);
//        return this;
//    }
//
//    /**
//     * 쿼리 파라미터를 추가합니다.
//     * @param key 파라미터 이름
//     * @param value 파라미터 값
//     * @return 빌더 인스턴스
//     */
//    public ApiRequestBuilder queryParam(String key, String value) {
//        this.queryParams.put(key, value);
//        return this;
//    }
//
//    /**
//     * URI 경로 변수를 설정합니다. 예: "/users/{userId}" -> .pathVariable("userId", "123")
//     * @param name 경로 변수 이름 (예: "userId")
//     * @param value 경로 변수에 대체될 값
//     * @return 빌더 인스턴스
//     */
//    public ApiRequestBuilder pathVariable(String name, Object value) {
//        this.uri = this.uri.replace("{" + name + "}", String.valueOf(value));
//        return this;
//    }
//
//    /**
//     * 이 요청에 적용할 재시도 정책을 설정합니다. (선택 사항, 기본값: WebClientPool의 기본 정책)
//     * @param policy 재시도 정책 Enum
//     * @return 빌더 인스턴스
//     */
//    public ApiRequestBuilder retry(RetryPolicy policy) {
//        this.retryPolicy = policy;
//        return this;
//    }
//
//    /**
//     * 이 요청에 적용할 타임아웃 정책을 설정합니다. (선택 사항, 기본값: WebClientPool의 기본 정책)
//     * @param policy 타임아웃 정책 Enum
//     * @return 빌더 인스턴스
//     */
//    public ApiRequestBuilder timeout(TimeoutPolicy policy) {
//        this.timeoutPolicy = policy;
//        return this;
//    }
//
//    /**
//     * 구성된 API 요청을 실행하고, 동기적으로 응답을 받아옵니다.
//     *
//     * @param responseType 응답 바디를 매핑할 DTO 클래스 타입
//     * @param <T> 응답 바디 DTO 타입
//     * @return API 응답을 포함하는 ApiResponse 객체
//     */
//    public <T> ApiResponse<T> send(Class<T> responseType) {
//        // HTTP Method에 따른 URI 구성
//        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath(uri);
//        queryParams.forEach(uriBuilder::queryParam);
//        String finalUri = uriBuilder.build().toUriString(); // 최종 URI 문자열을 미리 생성
//
//        // --- 이 부분이 수정되었습니다: if-else if 문으로 변경 ---
//        WebClient.RequestHeadersSpec<?> requestHeadersSpec; // 가장 범용적인 헤더 스펙으로 선언
//
//        if (method == HttpMethod.GET) {
//            requestHeadersSpec = webClient.get().uri(finalUri);
//        } else if (method == HttpMethod.POST) {
//            requestHeadersSpec = webClient.post().uri(finalUri);
//        } else if (method == HttpMethod.PUT) {
//            requestHeadersSpec = webClient.put().uri(finalUri);
//        } else if (method == HttpMethod.DELETE) {
//            requestHeadersSpec = webClient.delete().uri(finalUri);
//        } else {
//            throw new UnsupportedOperationException("Unsupported HTTP method: " + method);
//        }
//
//        // 헤더 추가
//        headers.forEach(requestHeadersSpec::header);
//
//        // 바디 추가 (POST, PUT 등)
//        WebClient.RequestHeadersSpec<?> finalRequestSpec; // 바디가 추가될 수도 있으므로 최종 스펙 변수
//        if (body != null && (method == HttpMethod.POST || method == HttpMethod.PUT)) {
//            // RequestBodySpec으로 캐스팅하여 bodyValue() 호출 가능하게 함
//            finalRequestSpec = ((WebClient.RequestBodySpec) requestHeadersSpec).bodyValue(body);
//        } else {
//            finalRequestSpec = requestHeadersSpec;
//        }
//
//        try {
//            Mono<T> responseMono = finalRequestSpec.retrieve() // 최종 스펙 사용
//                                                   .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse ->
//                                                           clientResponse.bodyToMono(CommonErrorResponse.class)
//                                                                         .defaultIfEmpty(new CommonErrorResponse("UNKNOWN_ERROR", "No error body provided"))
//                                                                         .flatMap(errorBodyDto -> {
//                                                                             logger.error("API Call Error: {} - Code: {}, Message: {}",
//                                                                                          clientResponse.statusCode(), errorBodyDto.code, errorBodyDto.message);
//                                                                             return Mono.error(new ApiClientException(
//                                                                                     "API request failed with status " + clientResponse.statusCode().value(),
//                                                                                     clientResponse.statusCode().value(),
//                                                                                     errorBodyDto.message,
//                                                                                     errorBodyDto
//                                                                             ));
//                                                                         }))
//                                                   .bodyToMono(responseType);
//
//            if (retryPolicy.getAttempts() > 0) {
//                logger.debug("Applying retry policy: {} attempts", retryPolicy.getAttempts());
//                responseMono = responseMono.retry(retryPolicy.getAttempts());
//            }
//
//            logger.debug("Applying timeout policy: {} seconds", timeoutPolicy.getSeconds());
//            responseMono = responseMono.timeout(java.time.Duration.ofSeconds(timeoutPolicy.getSeconds()));
//
//            T responseBody = responseMono.block();
//            return ApiResponse.success(responseBody, 200); // TODO: 실제 HTTP 상태 코드 반영
//
//        } catch (ApiClientException e) {
//            logger.error("ApiClientException caught: {} - Status: {}, Body: {}",
//                         e.getMessage(), e.getStatusCode(), e.getResponseBody());
//            return ApiResponse.failure(e.getErrorDetails(), e.getStatusCode());
//        } catch (WebClientResponseException e) {
//            logger.error("WebClientResponseException caught (possibly unhandled status): {} - Status: {}, Body: {}",
//                         e.getMessage(), e.getStatusCode(), e.getResponseBodyAsString());
//            return ApiResponse.failure(e.getResponseBodyAsString(), e.getStatusCode().value());
//        } catch (Exception e) {
//            logger.error("API Call Failed with unexpected error: {}", e.getMessage(), e);
//            return ApiResponse.failure("An unexpected error occurred: " + e.getMessage(), -1);
//        }
//    }
//}
