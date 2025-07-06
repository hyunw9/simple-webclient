package client.config;

import io.netty.handler.codec.http.HttpMethod;

public class IntegrationActionConfig {
    private String domain;
    private String action;
    private String serviceId; // 대상 service_id
    private HttpMethod method;
    private String pathTemplate;
    private String version;
    private String status;
    private String processingMode;

    public IntegrationActionConfig() {}

    // AllArgsConstructor (필요시)
    public IntegrationActionConfig(String domain, String action, String serviceId, HttpMethod method, String pathTemplate, String version, String status, String processingMode) {
        this.domain = domain;
        this.action = action;
        this.serviceId = serviceId;
        this.method = method;
        this.pathTemplate = pathTemplate;
        this.version = version;
        this.status = status;
        this.processingMode = processingMode;
    }

    // Getters and Setters
    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public HttpMethod getMethod() { return method; }
    public void setMethod(HttpMethod method) { this.method = method; }
    public String getPathTemplate() { return pathTemplate; }
    public void setPathTemplate(String pathTemplate) { this.pathTemplate = pathTemplate; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getProcessingMode() { return processingMode; }
    public void setProcessingMode(String processingMode) { this.processingMode = processingMode; }

    @Override
    public String toString() {
        return "IntegrationActionConfig{" +
               "domain='" + domain + '\'' +
               ", action='" + action + '\'' +
               ", serviceId='" + serviceId + '\'' +
               ", method=" + method +
               ", pathTemplate='" + pathTemplate + '\'' +
               '}';
    }
}
