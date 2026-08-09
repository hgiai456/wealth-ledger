package com.giaidev.identity.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.giaidev.identity.enums.UserStatus;
import com.giaidev.identity.validator.DobConstraint;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {
    @Size(min = 8, message = "PASSWORD_INVALID") // ít nhất 8 ký tự nếu không thì trả ra message lỗi
    String password;
    String firstName;
    String lastName;

    @Email(message = "INVALID_EMAIL")
    String email;

    UserStatus status;
    @DobConstraint(min = 18, message = "INVALID_DOB") // custom annotation
    LocalDate dob;
    List<String> roles;
}