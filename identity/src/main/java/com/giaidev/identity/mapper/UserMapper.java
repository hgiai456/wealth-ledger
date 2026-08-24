package com.giaidev.identity.mapper;

import java.util.List;

import com.giaidev.identity.dto.request.UserCreationRequest;
import com.giaidev.identity.dto.request.UserUpdateRequest;
import com.giaidev.identity.dto.response.UserResponse;
import com.giaidev.identity.entity.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring")
public interface UserMapper {
    // Khởi tạo hàm toUser => Hàm này dùng để chuyển đổi từ request => entity để lưu dữ liệu vào db
    User toUser(UserCreationRequest request);
    //    @Mapping(target = "lastName", ignore = true)
    // Khởi tạo hàm toUserResponse => Hàm này dùng để chuyển đổi từ Entity => Response để trả về Client
    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponse(List<User> users);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}