package com.wsd.ecommerce.presenter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum ApiResponseCode {

    OPERATION_SUCCESSFUL("S200"),
    RECORD_NOT_FOUND("ECF404"),
    INVALID_REQUEST_DATA("ECF400"),
    INTER_SERVICE_COMMUNICATION_ERROR("ECF503"),
    DB_OPERATION_FAILED("ECF422"),
    UNHANDLED_EXCEPTION("ECF500"),
    METHOD_NOT_ALLOWED("ECF405"),
    UNAUTHORIZED_RESOURCE_ACCESS("ECF401"),
    ;

    private final String responseCode;

    public static boolean isOperationSuccessful(ApiResponse apiResponse) {
        return Objects.nonNull(apiResponse) && apiResponse.getResponseCode().equals(ApiResponseCode.OPERATION_SUCCESSFUL.getResponseCode());
    }

    public static boolean isNotOperationSuccessful(ApiResponse apiResponse) {
        return !isOperationSuccessful(apiResponse);
    }

}
