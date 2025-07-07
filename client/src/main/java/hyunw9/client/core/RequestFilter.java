package hyunw9.client.core;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface RequestFilter {
    CompletionStage<Response> apply(RequestSpec spec, Chain next);

    interface Chain {
        CompletionStage<Response> proceed(RequestSpec spec);
    }

    record Response(int status, String rawBody, Map<String, String> headers){}
}
