package hyunw9.client.action;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import hyunw9.client.config.ConfigActionResolver;
import hyunw9.client.core.RetryOption;
import hyunw9.client.core.TimeoutOption;
import hyunw9.client.discovery.ServiceDiscovery;
import hyunw9.client.util.TemplateUtil;
import hyunw9.client.webclient.Requester;

public final class ActionExecutor {

	private final ServiceDiscovery discovery;
	private final ConfigActionResolver config;

	public ActionExecutor(ServiceDiscovery d, ConfigActionResolver c) {
		this.discovery = d;
		this.config = c;
	}

	public <T> CompletableFuture<T> action(
		String domain, String action,
		Map<String, Object> pathVars, Map<String, Object> queryVars, Object body,
		Class<T> resType, RetryOption retry, TimeoutOption to) {

		final URI base = discovery.resolve(domain);

		return config.meta(domain, action).thenCompose(meta -> {
			final String path = TemplateUtil.resolve(meta.pathTemplate(), pathVars);
			final Requester.RequestBuilder req = Requester.request(base.resolve(path).toString())
				.method(meta.method())
				.retry(retry)
				.timeout(to);

			meta.headers().forEach(req::header);
			meta.defaultParams().forEach(req::param);
			queryVars.forEach(req::param);
			if (body != null) {
				req.body(body);
			}

			return req.async(resType);
		});
	}
}
