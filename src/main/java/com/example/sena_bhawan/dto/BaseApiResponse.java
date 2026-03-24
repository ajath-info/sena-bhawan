package com.example.sena_bhawan.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private int statusCode;
    
    // Success response
    public static <T> BaseApiResponse<T> success(T data, String message) {
        return new BaseApiResponse<>(true, message, data, 200);
    }
    
    public static <T> BaseApiResponse<T> success(T data) {
        return new BaseApiResponse<>(true, "Success", data, 200);
    }
    
    // Error response
    public static <T> BaseApiResponse<T> error(String message, int statusCode) {
        return new BaseApiResponse<>(false, message, null, statusCode);
    }
    
    public static <T> BaseApiResponse<T> error(String message) {
        return new BaseApiResponse<>(false, message, null, 400);
    }
    
    public static <T> BaseApiResponse<T> badRequest(String message) {
        return new BaseApiResponse<>(false, message, null, 400);
    }
    
    public static <T> BaseApiResponse<T> notFound(String message) {
        return new BaseApiResponse<>(false, message, null, 404);
    }
    
    public static <T> BaseApiResponse<T> internalError(String message) {
        return new BaseApiResponse<>(false, message, null, 500);
    }
}