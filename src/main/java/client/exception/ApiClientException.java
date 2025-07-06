package client.exception;

import java.io.Serial;

import client.common.ApiResponse.CommonErrorResponse;

public class ApiClientException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1298544147093314934L;
    private final int statusCode;
    private final String responseBody;
    private final CommonErrorResponse errorDetails;

    public ApiClientException(String message, int statusCode, String responseBody, CommonErrorResponse errorDetails) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.errorDetails = errorDetails;
    }

    public ApiClientException(String message, Throwable cause, int statusCode, String responseBody, CommonErrorResponse errorDetails) {
        super(message, cause);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.errorDetails = errorDetails;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public CommonErrorResponse getErrorDetails() {
        return errorDetails;
    }
}
