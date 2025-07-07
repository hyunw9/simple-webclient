//package client;
//
//import java.util.List;
//import java.util.Random;
//import java.util.stream.Collectors;
//
//import org.springframework.web.reactive.function.client.WebClient;
//
//import client.config.IntegrationActionConfig;
//import client.config.ServiceInstanceConfig;
//import client.config.WebClientPool;
//import io.netty.handler.codec.http.HttpMethod;
//
///**
// * 외부 API 호출을 위한 최상위 클라이언트 진입점.
// * 특정 서비스에 대한 API 호출 빌더를 제공합니다.
// */
//public class ApiClient {
//
//    private final WebClient webClient; // 서비스별로 구성된 WebClient 인스턴스
//
//    // 외부에서 직접 인스턴스화하지 못하도록 private 생성자
//    private ApiClient(WebClient webClient) {
//        this.webClient = webClient;
//    }
//
//    /**
//     * 특정 서비스 이름으로 API 클라이언트 인스턴스를 가져옵니다.
//     * WebClientPool에서 해당 서비스에 맞는 WebClient를 주입받습니다.
//     *
//     * @param serviceName 설정 파일에 정의된 서비스 이름 (예: "user-service", "product-service")
//     * @return 해당 서비스에 특화된 ApiClient 인스턴스
//     */
//    public static ApiClient forService(String serviceName) {
//        WebClient webClient = WebClientPool.getClient(serviceName);
//        return new ApiClient(webClient);
//    }
//
//    // HTTP GET 요청 빌더 시작
//    public ApiRequestBuilder get(String uri) {
//        return new ApiRequestBuilder(webClient, HttpMethod.GET, uri);
//    }
//
//    // HTTP POST 요청 빌더 시작
//    public ApiRequestBuilder post(String uri) {
//        return new ApiRequestBuilder(webClient, HttpMethod.POST, uri);
//    }
//
//    // HTTP PUT 요청 빌더 시작
//    public ApiRequestBuilder put(String uri) {
//        return new ApiRequestBuilder(webClient, HttpMethod.PUT, uri);
//    }
//
//    // HTTP DELETE 요청 빌더 시작
//    public ApiRequestBuilder delete(String uri) {
//        return new ApiRequestBuilder(webClient, HttpMethod.DELETE, uri);
//    }
//
//    // TODO: 필요하다면 PATCH 등 다른 HTTP 메서드 추가
//    /**
//     * 특정 통합 액션 (domain, action)에 대한 API 호출 빌더를 시작합니다.
//     * 이 메서드는 로드 밸런싱을 수행하고, 액션에 정의된 HTTP 메서드와 경로 템플릿을 사용합니다.
//     *
//     * @param domain 통합 액션의 도메인 (예: "user")
//     * @param action 통합 액션의 이름 (예: "createUser")
//     * @return API 호출 빌더
//     * @throws IllegalArgumentException 해당 액션이 정의되지 않았거나 유효한 인스턴스가 없을 경우
//     */
//    public ApiRequestBuilder action(String domain, String action) {
//        String actionKey = domain + "." + action;
//        IntegrationActionConfig actionConfig = serviceConfig.getActions().get(actionKey);
//
//        if (actionConfig == null) {
//            throw new IllegalArgumentException("Integration action '" + actionKey + "' not found for service '" + serviceConfig.getServiceId() + "'.");
//        }
//        if (!actionConfig.getStatus().equalsIgnoreCase("ACTIVE")) {
//            throw new IllegalStateException("Integration action '" + actionKey + "' is not active.");
//        }
//
//        // 로드 밸런싱: UP 상태의 인스턴스 중 하나를 선택
//        List<ServiceInstanceConfig> upInstances = serviceConfig.getInstances().values().stream()
//                                                               .filter(instance -> "UP".equalsIgnoreCase(instance.getStatus()))
//                                                               .collect(Collectors.toList());
//
//        if (upInstances.isEmpty()) {
//            throw new IllegalStateException("No 'UP' service instances found for service '" + serviceConfig.getServiceId() + "'.");
//        }
//
//        // TODO: 실제 로드 밸런싱 알고리즘 (Round Robin, Least Connections, Weighted Random 등) 구현
//        // 현재는 간단한 Random 선택
//        ServiceInstanceConfig selectedInstance = upInstances.get(new Random().nextInt(upInstances.size()));
//        logger.debug("Selected instance for service {}: {}", serviceConfig.getServiceId(), selectedInstance.getBaseUrl());
//
//        // WebClientPool로부터 선택된 인스턴스에 대한 WebClient를 가져옴
//        // 이때, ServiceConfig의 기본 정책을 WebClientPool에 전달하여 캐시 키에 반영
//        WebClient webClient = WebClientPool.getClient(selectedInstance, serviceConfig.getDefaultTimeoutPolicy(), serviceConfig.getDefaultRetryPolicy());
//
//        // OutboxApiRequestBuilder 생성 시, 액션의 메서드와 경로 템플릿,
//        // 그리고 ServiceConfig의 기본 정책 (오버라이드 가능하도록)을 전달
//        return new ApiRequestBuilder(
//                webClient,
//                actionConfig.getMethod(),
//                actionConfig.getPathTemplate(),
//                serviceConfig.getDefaultRetryPolicy(), // ServiceConfig의 기본 정책
//                serviceConfig.getDefaultTimeoutPolicy() // ServiceConfig의 기본 정책
//        );
//    }
//}
