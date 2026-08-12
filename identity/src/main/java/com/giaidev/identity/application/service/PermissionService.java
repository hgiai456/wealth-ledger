package com.giaidev.identity.application.service;

import java.util.List;

import com.giaidev.identity.web.request.PermissionRequest;
import com.giaidev.identity.web.response.PermissionResponse;
import com.giaidev.identity.domain.entity.Permission;
import com.giaidev.identity.mapper.PermissionMapper;
import com.giaidev.identity.infrastructure.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        var permissios = permissionRepository.findAll();
        return permissios.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}