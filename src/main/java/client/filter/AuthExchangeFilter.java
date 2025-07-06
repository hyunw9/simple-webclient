package client.filter;

import java.util.function.Supplier;

import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;

import reactor.core.publisher.Mono;

/**
 * API 요청에 인증 토큰을 추가하는 필터.
 * 토큰 공급자(Supplier<Mono<String>>)를 통해 동적으로 토큰을 주입받습니다.
 */
public final class AuthExchangeFilter implements ExchangeFilterFunction {

    private final Supplier<Mono<String>> tokenSupplier;

    private AuthExchangeFilter(Supplier<Mono<String>> tokenSupplier) {
        this.tokenSupplier = tokenSupplier;
    }

    public static AuthExchangeFilter authFilter(Supplier<Mono<String>> tokenSupplier) {
        return new AuthExchangeFilter(tokenSupplier);
    }

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return tokenSupplier.get()
                            .flatMap(token -> {
                                // if token is present, add it to the request
                                ClientRequest filteredRequest = ClientRequest.from(request)
                                                                             .header("Authorization", "Bearer " + token)
                                                                             .build();
                                return next.exchange(filteredRequest);
                            })
                            .switchIfEmpty(next.exchange(request)); // 토큰이 없으면 그냥 요청 진행
    }
}
