package hyunw9.client;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import hyunw9.client.action.ActionExecutor;
import hyunw9.client.config.ConfigActionResolver;
import hyunw9.client.core.HttpMethod;
import hyunw9.client.core.RetryOption;
import hyunw9.client.core.TimeoutOption;
import hyunw9.client.discovery.ServiceDiscovery;
import hyunw9.client.util.JsonUtil;
import hyunw9.client.webclient.Requester;

public class DemoScenario {

    public static void main(String[] args) {

        /* ───────────────────────────────────────────────
         * 0. 런타임 인프라 객체 (Eureka + Config-Server)
         * ───────────────────────────────────────────────*/
        ServiceDiscovery discovery = name -> URI.create("http://localhost:8761");
        ConfigActionResolver config = new ConfigActionResolver("http://localhost:8761");

        ActionExecutor actions = new ActionExecutor(discovery, config);
        //
        // /* ───────────────────────────────────────────────
        //  * 1. 동기 Example  ― ‘issueInvoice’
        //  *    - POST /actions/issueInvoice
        //  *    - 쿼리파라미터 version=v1
        //  *    - Retry 3회, Fixed 300 ms
        //  * ───────────────────────────────────────────────*/
        // InvoiceReq reqBody = new InvoiceReq(1L, 9_000);
        // InvoiceDto invoice = actions.action(
        //     "billing",
        //     "issueInvoice",
        //     Map.of(),                       // path variables
        //     Map.of("version", "v1"),        // query params
        //     reqBody,
        //     InvoiceDto.class,
        //     RetryOption.RETRY_3_FIXED_300MS,
        //     TimeoutOption.TIMEOUT_2_SEC
        // ).join();
        //
        // System.out.println("Issued invoice → " + JsonUtil.toJson(invoice));
        //
        // /* ───────────────────────────────────────────────
        //  * 2. 비동기 Example ― ‘findInvoice’
        //  *    - GET /actions/invoice/{invoiceId}
        //  *    - Timeout 1 s (실패 시 예외)
        //  * ───────────────────────────────────────────────*/
        // CompletableFuture<InvoiceDto> asyncFind = actions.action(
        //     "billing",
        //     "findInvoice",
        //     Map.of("invoiceId", invoice.invoiceId()),
        //     Map.of(),
        //     null,
        //     InvoiceDto.class,
        //     RetryOption.NO_RETRY,
        //     TimeoutOption.TIMEOUT_1_SEC
        // );
        //
        // asyncFind.whenComplete((inv, err) -> {
        //     if (err == null) {
        //         System.out.println("Fetched invoice → " + JsonUtil.toJson(inv));
        //     } else {
        //         err.printStackTrace();
        //     }
        // }).join();

        /* ───────────────────────────────────────────────
         * 3. 직접 Requester 사용 ― 외부 OAuth 예시
         *    (OAuthFilter 예시는 별도 모듈이라 가정)
         * ───────────────────────────────────────────────*/
        //todo: port()도 받을 수 있게, 그리고 .api()로 uri 명시할 수 있게 하면 좋을듯.
        String userJson = Requester.request("http://localhost:8080/health")
            .method(HttpMethod.GET)
            // .header("Content-Type", "text/plain")
            // .retry(RetryOption.RETRY_3_EXP_BACKOFF)
            // .timeout(Duration.ofSeconds(3))
            .send(String.class);
            // .send(String.class);   // 결과를 RAW JSON으로 받고 싶을 때

        System.out.println("GitHub user → " + userJson);

        /* ───────────────────────────────────────────────
         * 4. 커스텀 파라미터 + 헤더 오버라이드 Example
         *    - Config 기본 헤더(X-Trace) + 수행 시점 헤더(Marker) 합산
         * ───────────────────────────────────────────────*/
        CompletableFuture<Void> postMsg = actions.action(
            "community",
            "postMessage",
            Map.of(),
            Map.of(),
            new PostReq("Hello, world!"),
            Void.class,
            RetryOption.RETRY_5_EXP_BACKOFF,
            TimeoutOption.TIMEOUT_5_SEC
        ).thenRun(() -> System.out.println("Message posted!"));

        postMsg.join();
    }

    /* ===== DTO 샘플 ===== */
    record InvoiceReq(long userId, int amount) {}
    record InvoiceDto(long invoiceId, String status) {}
    record PostReq(String message) {}
}
