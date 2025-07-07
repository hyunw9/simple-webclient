package hyunw9.client.discovery;

import java.net.URI;

@FunctionalInterface
public interface ServiceDiscovery {
	URI resolve(String name);
}
