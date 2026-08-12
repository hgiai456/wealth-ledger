package com.giaidev.identity.mapper;

import com.giaidev.identity.web.request.PermissionRequest;
import com.giaidev.identity.web.response.PermissionResponse;
import com.giaidev.identity.domain.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}