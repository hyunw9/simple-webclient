package hyunw9.client.config;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.web.reactive.function.client.WebClient;

import hyunw9.client.meta.ActionMeta;
import hyunw9.client.util.JsonUtil;

public final class ConfigActionResolver {

	private final WebClient client = WebClient.builder().build();
	private final String base;                    // ex) http://config-server
	private final ConcurrentMap<String, Map<String, ActionMeta>> cache = new ConcurrentHashMap<>();

	public ConfigActionResolver(String base){ this.base=base; }

	public CompletableFuture<ActionMeta> meta(String domain,String action){
		return CompletableFuture.supplyAsync(()-> cache.computeIfAbsent(domain,this::load))
			.thenApply(m-> m.get(action));
	}
	private Map<String,ActionMeta> load(String domain){
		final String json = client.get()
			.uri(base+ '/' +domain+"/default")
			.retrieve().bodyToMono(String.class).block();
		record Wrapper(Map<String,ActionMeta> actions){}
		return JsonUtil.fromJson(json, Wrapper.class).actions();
	}
}
