package com.giaidev.identity.web.response;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

import com.giaidev.identity.domain.enums.UserStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
// Nếu khởi tạo annotation @Data thi tren class thi se tao luon
// => Getter & Setter & Constructor(bắt buộc có attribute)
@NoArgsConstructor // Constructor rỗng
@AllArgsConstructor // Constructor có attribute
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE) // Neu khong khai bao thi private
public class UserResponse {
    String id;
    String username;
    String firstName;
    String lastName;
    LocalDate dob;
    String email;
    UserStatus status;
    Instant createdAt;
    Instant updatedAt;
    Set<RoleResponse> roles;
}