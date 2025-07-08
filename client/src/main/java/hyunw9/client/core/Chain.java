package hyunw9.client.core;

import java.util.concurrent.CompletionStage;

public interface Chain {
	CompletionStage<RequestFilter.Response> proceed(RequestSpec spec);
}
