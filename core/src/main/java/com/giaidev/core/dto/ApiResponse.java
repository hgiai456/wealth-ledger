package com.giaidev.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // Nếu field không có giá trị thì ẩn luôn
public class ApiResponse<T> {
    @Default
    boolean success = true;

    @Default
    int code = 1000;

    String message;
    T result;

    Map<String, String> errors;

    public static <T> ApiResponse<T> success(T result)
    {
        return ApiResponse.<T>builder()
                .success(true)
                .code(1000)
                .result(result)
                .build();
    }

    public static ApiResponse<Void> error(int code, String message){
        return ApiResponse.<Void>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }

    public static ApiResponse<Void> validationError(
            int code,
            String message,
            Map<String, String> errors
    ) {
        return ApiResponse.<Void>builder()
                .success(false)
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }


}