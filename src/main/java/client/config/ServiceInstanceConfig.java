package client.config;

import com.fasterxml.jackson.databind.JsonNode;

public class ServiceInstanceConfig {
    private String id; // DB의 id (인스턴스 고유 식별자)
    private String serviceId; // 부모 service_id
    private String scheme;
    private String host;
    private int port;
    private int weight;
    private String status;
    private JsonNode metadata; // JSONB 컬럼 매핑

    public ServiceInstanceConfig() {}

    // AllArgsConstructor (필요시)
    public ServiceInstanceConfig(String id, String serviceId, String scheme, String host, int port, int weight, String status, JsonNode metadata) {
        this.id = id;
        this.serviceId = serviceId;
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.weight = weight;
        this.status = status;
        this.metadata = metadata;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getScheme() { return scheme; }
    public void setScheme(String scheme) { this.scheme = scheme; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public JsonNode getMetadata() { return metadata; }
    public void setMetadata(JsonNode metadata) { this.metadata = metadata; }

    public String getBaseUrl() {
        return String.format("%s://%s:%d", scheme, host, port);
    }

    @Override
    public String toString() {
        return "ServiceInstanceConfig{" +
               "id='" + id + '\'' +
               ", baseUrl='" + getBaseUrl() + '\'' +
               ", status='" + status + '\'' +
               '}';
    }
}
