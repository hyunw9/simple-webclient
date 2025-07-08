package hyunw9.client.transport;

import java.util.concurrent.CompletionStage;

import hyunw9.client.core.RequestFilter;
import hyunw9.client.core.RequestSpec;

public interface HttpTransport {
	CompletionStage<hyunw9.client.core.RequestFilter.Response> send(RequestSpec requestSpec);
}
