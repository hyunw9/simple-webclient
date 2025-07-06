package client.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {
    public static <T> ApiResponse<T> success(T data, Integer statusCode) {
        return new ApiResponse<>(true, data, null, statusCode, null);
    }

    public static <T> ApiResponse<T> failure(String errorMessage, Integer statusCode) {
        return new ApiResponse<>(false, null, errorMessage, statusCode, null);
    }

    public static <T> ApiResponse<T> failure(CommonErrorResponse errorDetails, Integer statusCode) {
        return new ApiResponse<>(false, null, errorDetails != null ? errorDetails.message : "Unknown error",
                                 statusCode, errorDetails);
    }
    public final boolean success;
    public final T data;
    public final String errorMessage;
    public final Integer statusCode;
    public final CommonErrorResponse errorDetails;

    public ApiResponse(boolean success, T data, String errorMessage, Integer statusCode,
                       CommonErrorResponse errorDetails) {
        this.success = success;
        this.data = data;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
        this.errorDetails = errorDetails;
    }

    private ApiResponse() {
        this.success = false;
        this.data = null;
        this.errorMessage = null;
        this.statusCode = null;
        this.errorDetails = null;
    }

    public boolean isSuccess() {return success;}

    public T getData() {return data;}

    public String getErrorMessage() {return errorMessage;}

    public Integer getStatusCode() {return statusCode;}

    public CommonErrorResponse getErrorDetails() {return errorDetails;}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CommonErrorResponse {
        public String code;
        public String message;
        public String details; // 추가 필드 예시

        public CommonErrorResponse() {}

        public CommonErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getCode() {return code;}

        public String getMessage() {return message;}

        public String getDetails() {return details;}
    }
}

