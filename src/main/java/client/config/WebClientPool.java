//package client.config;
//
//import java.net.http.HttpClient;
//import java.time.Duration;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Supplier;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.client.reactive.ReactorClientHttpConnector;
//import org.springframework.web.reactive.function.client.ExchangeStrategies;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import client.common.RetryPolicy;
//import client.common.TimeoutPolicy;
//import client.filter.AuthExchangeFilter;
//import client.filter.LoggingExchangeFilter;
//import io.netty.channel.ChannelOption;
//import io.netty.handler.timeout.ReadTimeoutHandler;
//import io.netty.handler.timeout.WriteTimeoutHandler;
//import reactor.core.publisher.Mono;
//import reactor.netty.resources.ConnectionProvider;
//
//public class WebClientPool {
//    private static final Logger logger = LoggerFactory.getLogger(WebClientPool.class);
//    private static final Map<String, WebClient> clientCache = new ConcurrentHashMap<>();
//    private static Supplier<Mono<String>> globalAuthTokenSupplier;
//
//    // initialize 메서드 시그니처 변경: 초기 설정 맵을 받지 않음.
//    // 대신, AuthTokenSupplier만 받아서 WebClientPool 내부에 저장.
//    public static void initialize(Supplier<Mono<String>> authTokenSupplier) {
//        logger.info("WebClientPool initialized. Ready to create WebClients on demand.");
//        globalAuthTokenSupplier = authTokenSupplier;
//    }
//
//    // ServiceInstanceConfig 객체를 직접 받아서 WebClient를 반환하는 메서드
//    public static WebClient getClient(ServiceInstanceConfig instanceConfig, TimeoutPolicy overrideTimeout, RetryPolicy overrideRetry) {
//        // 캐시 키는 인스턴스 고유 ID와 오버라이드 정책을 포함하여 생성
//        String cacheKey = generateCacheKey(instanceConfig, overrideTimeout, overrideRetry);
//
//        return clientCache.computeIfAbsent(cacheKey, k -> {
//            logger.info("Creating new WebClient for instance config: {}", instanceConfig.getBaseUrl());
//
//            ConnectionProvider provider = ConnectionProvider.builder("custom-connection-pool-" + instanceConfig.getServiceId() + "-" + instanceConfig.getId())
//                                                            .maxConnections(100)
//                                                            .maxIdleTime(Duration.ofSeconds(60))
//                                                            .maxLifeTime(Duration.ofSeconds(600))
//                                                            .pendingAcquireTimeout(Duration.ofSeconds(45))
//                                                            .build();
//
//            // 적용될 타임아웃 정책 (오버라이드된 것이 있다면 그것을 사용)
//            Duration effectiveTimeout = overrideTimeout != null ? overrideTimeout.getDuration() : instanceConfig.getMetadataTimeoutPolicy().getDuration();
//
//            HttpClient httpClient = HttpClient.create(provider)
//                                              .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) effectiveTimeout.toMillis()) // 연결 타임아웃
//                                              .doOnConnected(conn -> conn
//                                                      .addHandlerLast(new ReadTimeoutHandler(effectiveTimeout.getSeconds(), TimeUnit.SECONDS))
//                                                      .addHandlerLast(new WriteTimeoutHandler(effectiveTimeout.getSeconds(), TimeUnit.SECONDS)));
//
//            WebClient.Builder webClientBuilder = WebClient.builder()
//                                                          .baseUrl(instanceConfig.getBaseUrl())
//                                                          .clientConnector(new ReactorClientHttpConnector(httpClient))
//                                                          .exchangeStrategies(ExchangeStrategies.builder()
//                                                                                                .codecs(clientCodecConfigurer -> clientCodecConfigurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
//                                                                                                .build());
//
//            webClientBuilder.filter(LoggingExchangeFilter.create());
//
//            // 인증 필요 여부는 ServiceConfig의 defaultAuthRequired와 instanceConfig의 metadata를 조합하여 판단
//            boolean instanceAuthRequired = instanceConfig.isAuthRequiredFromMetadata(); // ServiceInstanceConfig에 추가될 메서드
//            if (instanceAuthRequired && globalAuthTokenSupplier != null) {
//                webClientBuilder.filter(AuthExchangeFilter.authFilter(globalAuthTokenSupplier));
//            } else if (instanceAuthRequired && globalAuthTokenSupplier == null) {
//                logger.warn("Auth required for service instance {} but no globalAuthTokenSupplier provided to WebClientPool.", instanceConfig.getId());
//            }
//
//            return webClientBuilder.build();
//        });
//    }
//
//    private static String generateCacheKey(ServiceInstanceConfig config, TimeoutPolicy overrideTimeout, RetryPolicy overrideRetry) {
//        return String.format("%s_%s_%s_%s_%b",
//                             config.getBaseUrl(),
//                             config.getMetadataTimeoutPolicy().name(), // Metadata에서 파싱된 기본 정책
//                             config.getMetadataRetryPolicy().name(),   // Metadata에서 파싱된 기본 정책
//                             (overrideTimeout != null ? overrideTimeout.name() : "NULL"), // 오버라이드 정책도 키에 포함
//                             (overrideRetry != null ? overrideRetry.name() : "NULL"),
//                             config.isAuthRequiredFromMetadata()
//        );
//    }
//
//    public static void clearCache() {
//        clientCache.clear();
//        logger.info("WebClientPool cache cleared.");
//    }
//}
