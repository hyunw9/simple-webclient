package hyunw9.client.discovery;

import java.net.URI;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;

public class EurekaDiscovery implements ServiceDiscovery{

	private final EurekaClient client;
	public EurekaDiscovery(EurekaClient c){ this.client=c; }

	@Override public URI resolve(String name){
		final InstanceInfo instanceInfo = client.getNextServerFromEureka(name,false);
		return URI.create("http://"+instanceInfo.getIPAddr()+ ':' +instanceInfo.getPort());
	}
}
