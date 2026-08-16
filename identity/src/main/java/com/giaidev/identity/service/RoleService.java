package com.giaidev.identity.service;

import com.giaidev.identity.repository.PermissionRepository;
import com.giaidev.identity.repository.RoleRepository;
import com.giaidev.identity.mapper.RoleMapper;
import com.giaidev.identity.dto.request.RoleRequest;
import com.giaidev.identity.dto.response.RoleResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor // Sẽ tạo 1 constructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);

        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        role = roleRepository.save(role);

        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository
                .findAll() // Lấy tất cả roles
                .stream()
                //               .map(roleMapper::toRoleResponse) //Nay la su dung Lamda de rut ngan code
                .map(role -> roleMapper.toRoleResponse(role)) // Chuyển đổi từ Role => RoleResponse
                .toList(); // Để chuyển thành List
    }

    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}