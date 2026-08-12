package com.giaidev.identity.application.service;

import java.util.HashSet;
import java.util.List;

import com.giaidev.core.exception.AppException;
import com.giaidev.core.exception.ErrorCode;
import com.giaidev.identity.domain.constant.PredefinedRole;
import com.giaidev.identity.web.request.UserCreationRequest;
import com.giaidev.identity.web.request.UserUpdateRequest;
import com.giaidev.identity.web.response.UserResponse;
import com.giaidev.identity.domain.entity.Role;
import com.giaidev.identity.domain.entity.User;
import com.giaidev.identity.mapper.UserMapper;
import com.giaidev.identity.infrastructure.repository.RoleRepository;
import com.giaidev.identity.infrastructure.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor // Sẽ tạo 1 constructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    public UserResponse createUser(UserCreationRequest request) {

        log.info("Create user - UserService");

        if (userRepository.existsByUsername((request.getUsername())))
            throw new AppException(ErrorCode.USER_EXISTED); // Gọi lỗi bằng Enum lỗi

        // Chuyển đổi tu Request thành Entity(Object)
        User user = userMapper.toUser(request); // request.getPassword() => password user gui xuong
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Sử dụng Mapper: Chuyển đổi Entity => Response, tiện lưu vào db luôn
        // userRepository.save(user) sau khi thực hiện sẽ trả về 1 entity
        return userMapper.toUserResponse(user);
    }

    //    @PreAuthorize("hasRole('ADMIN')") //Khi dung HasRole thi xac thuc scope co ROLE_ truoc
    // annotation check trong scope cua token JWT
    // có phải là ADMIN không mới được quyền truy cập
    // PreAuthorize sẽ kiểm tra trước khi vào method
    //    @PreAuthorize("hasAuthority('APPROVE_POST')")
    @PreAuthorize("hasRole('ADMIN')")
    // khi dung hasAuthority thi xac thuc scope khong can ROLE_
    // (thuong su dung de authorize permission)
    // Nhưng sẽ có nhược điểm khi mà ta đưa hết thông tin scope vào token thì => dẫn đến kích thước token nặng => thì
    // token sẽ đặt trong header(Giới hạn 4 kiloByte) - Dẫn đến nặng hệ thống
    public List<UserResponse> getUsers() {
        log.info("In method get users");
        return userMapper.toUserResponse(userRepository.findAll());
    }

    @PostAuthorize("returnObject.username == authentication.name")
    // PostAuthorize là nó sẽ kiểm tra khi method được thực hiện xong nếu như bạn thỏa điều kiện
    // thì return còn không thì chặn lại
    // Điều kiện cho phép chỉ lấy được thông tin của user đang đăng nhập hiện tại
    public UserResponse getUser(String id) {
        log.info("In method get user by Id");
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }
}