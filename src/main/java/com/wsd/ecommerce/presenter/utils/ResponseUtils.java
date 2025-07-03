package com.wsd.ecommerce.presenter.utils;


import com.wsd.ecommerce.presenter.model.ApiResponse;
import com.wsd.ecommerce.presenter.model.ApiResponseCode;

public class ResponseUtils {

    public static <T> ApiResponse<T> createSuccessResponseObject(String message) {
        ApiResponse apiResponse = new ApiResponse<T>();
        apiResponse.setResponseCode(ApiResponseCode.OPERATION_SUCCESSFUL.getResponseCode());
        apiResponse.setResponseMessage(message);
        return apiResponse;
    }

    public static <T> ApiResponse<T> createSuccessResponseObject(String message, T data) {
        ApiResponse apiResponse = new ApiResponse<T>();
        apiResponse.setResponseCode(ApiResponseCode.OPERATION_SUCCESSFUL.getResponseCode());
        apiResponse.setResponseMessage(message);
        apiResponse.setData(data);
        return apiResponse;
    }

    public static <T> ApiResponse<T> createApiResponse(String responseCode, String responseMessage, T data) {
        ApiResponse apiResponse = new ApiResponse<T>();
        apiResponse.setResponseCode(responseCode);
        apiResponse.setResponseMessage(responseMessage);
        apiResponse.setData(data);
        return apiResponse;
    }

    public static <T> ApiResponse<T> createApiResponse(String responseCode, String responseMessage) {
        ApiResponse apiResponse = new ApiResponse<T>();
        apiResponse.setResponseCode(responseCode);
        apiResponse.setResponseMessage(responseMessage);
        return apiResponse;
    }

}
