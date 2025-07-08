package hyunw9.client.core;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public interface RequestFilter {
    CompletionStage<Response> apply(RequestSpec spec, Chain next);

    record Response(int status, String rawBody, Map<String, String> headers){}
}
