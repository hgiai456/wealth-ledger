package com.giaidev.identity.mapper;

import java.util.List;

import com.giaidev.identity.web.request.UserCreationRequest;
import com.giaidev.identity.web.request.UserUpdateRequest;
import com.giaidev.identity.web.response.UserResponse;
import com.giaidev.identity.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {
    // Khởi tạo hàm toUser => Hàm này dùng để chuyển đổi từ request => entity để lưu dữ liệu vào db
    User toUser(UserCreationRequest request);
    //    @Mapping(target = "lastName", ignore = true)
    // Khởi tạo hàm toUserResponse => Hàm này dùng để chuyển đổi từ Entity => Response để trả về Client
    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponse(List<User> users);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}