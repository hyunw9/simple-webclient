package hyunw9.client.discovery;

// todo: 필요시 구현

// public class EurekaDiscovery implements ServiceDiscovery{
//
// 	private final EurekaClient client;
// 	public EurekaDiscovery(EurekaClient c){ this.client=c; }
//
// 	@Override public URI resolve(String name){
// 		final InstanceInfo instanceInfo = client.getNextServerFromEureka(name,false);
// 		return URI.create("http://"+instanceInfo.getIPAddr()+ ':' +instanceInfo.getPort());
// 	}
// }
