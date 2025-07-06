package client.config;

import java.util.Map;
import java.util.Objects;

import client.common.RetryPolicy;
import client.common.TimeoutPolicy;

public class ServiceConfig {
    private String serviceId; // DB의 service_id (논리적 이름)
    private String description;
    private String owner;
    private String status;

    // 이 서비스의 기본 타임아웃/재시도 정책 (metadata JSONB에서 파싱될 수 있음)
    private TimeoutPolicy defaultTimeoutPolicy;
    private RetryPolicy defaultRetryPolicy;
    private boolean defaultAuthRequired; // 이 서비스에 대한 기본 인증 필요 여부

    // 이 서비스에 속한 인스턴스들의 맵 (키: instance_id 또는 scheme_host_port)
    // Spring Cloud Config에서 external.services.<service_id>.instances.<instance_id> 로 매핑
    private Map<String, ServiceInstanceConfig> instances;

    // 이 서비스와 연동되는 액션들의 맵 (키: domain.action)
    // Spring Cloud Config에서 external.services.<service_id>.actions.<domain>.<action> 로 매핑
    private Map<String, IntegrationActionConfig> actions;

    public ServiceConfig() {
        this.defaultTimeoutPolicy = TimeoutPolicy.MEDIUM;
        this.defaultRetryPolicy = RetryPolicy.NO_RETRY;
        this.defaultAuthRequired = false;
    }

    // AllArgsConstructor (필요시)
    public ServiceConfig(String serviceId, String description, String owner, String status,
                         TimeoutPolicy defaultTimeoutPolicy, RetryPolicy defaultRetryPolicy, boolean defaultAuthRequired,
                         Map<String, ServiceInstanceConfig> instances, Map<String, IntegrationActionConfig> actions) {
        this.serviceId = serviceId;
        this.description = description;
        this.owner = owner;
        this.status = status;
        this.defaultTimeoutPolicy = Objects.requireNonNullElse(defaultTimeoutPolicy, TimeoutPolicy.MEDIUM);
        this.defaultRetryPolicy = Objects.requireNonNullElse(defaultRetryPolicy, RetryPolicy.NO_RETRY);
        this.defaultAuthRequired = defaultAuthRequired;
        this.instances = instances;
        this.actions = actions;
    }

    // Getters and Setters (Spring @ConfigurationProperties 바인딩을 위해 필수)
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public TimeoutPolicy getDefaultTimeoutPolicy() { return defaultTimeoutPolicy; }
    public void setDefaultTimeoutPolicy(TimeoutPolicy defaultTimeoutPolicy) {this.defaultTimeoutPolicy = defaultTimeoutPolicy; }
    public RetryPolicy getDefaultRetryPolicy() { return defaultRetryPolicy; }
    public void setDefaultRetryPolicy(RetryPolicy defaultRetryPolicy) { this.defaultRetryPolicy = defaultRetryPolicy; }
    public boolean isDefaultAuthRequired() { return defaultAuthRequired; }
    public void setDefaultAuthRequired(boolean defaultAuthRequired) { this.defaultAuthRequired = defaultAuthRequired; }
    public Map<String, ServiceInstanceConfig> getInstances() { return instances; }
    public void setInstances(Map<String, ServiceInstanceConfig> instances) { this.instances = instances; }
    public Map<String, IntegrationActionConfig> getActions() { return actions; }
    public void setActions(Map<String, IntegrationActionConfig> actions) { this.actions = actions; }

    @Override
    public String toString() {
        return "ServiceConfig{" +
               "serviceId='" + serviceId + '\'' +
               ", description='" + description + '\'' +
               ", owner='" + owner + '\'' +
               ", status='" + status + '\'' +
               ", defaultTimeoutPolicy=" + defaultTimeoutPolicy +
               ", defaultRetryPolicy=" + defaultRetryPolicy +
               ", defaultAuthRequired=" + defaultAuthRequired +
               ", instances=" + instances.keySet() +
               ", actions=" + actions.keySet() +
               '}';
    }
}
