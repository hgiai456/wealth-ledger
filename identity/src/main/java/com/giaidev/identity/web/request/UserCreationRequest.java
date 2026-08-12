package com.giaidev.identity.web.request;

import java.time.LocalDate;

import com.giaidev.identity.web.validator.DobConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
// Nếu khởi tạo annotation @Data thi tren class thi se tao luon
// => Getter & Setter & Constructor(bắt buộc có attribute)
@NoArgsConstructor // Constructor rỗng
@AllArgsConstructor // Constructor có attribute
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE) // Neu khong khai bao thi private
public class UserCreationRequest {
    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    @Size(min = 8, message = "PASSWORD_INVALID") // ít nhất 8 ký tự nếu không thì trả ra message lỗi
    String password;

    @Email(message = "INVALID_EMAIL")
    String email;

    String firstName;
    String lastName;

    @DobConstraint(min = 16, message = "INVALID_DOB")
    LocalDate dob;
}