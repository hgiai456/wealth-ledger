package com.giaidev.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.Builder.Default;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // Nếu field không có giá trị thì ẩn luôn
public class ApiResponse<T> {
    @Default
    int code = 1000;

    String message;
    T result;
}