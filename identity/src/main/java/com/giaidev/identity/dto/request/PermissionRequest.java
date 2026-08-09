package com.giaidev.identity.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
// Nếu khởi tạo annotation @Data thi tren class thi se tao luon
// => Getter & Setter & Constructor(bắt buộc có attribute)
@NoArgsConstructor // Constructor rỗng
@AllArgsConstructor // Constructor có attribute
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRequest {
    String name;
    String description;
}