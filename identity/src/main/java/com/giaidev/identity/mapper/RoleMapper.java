package com.giaidev.identity.mapper;

import com.giaidev.identity.web.request.RoleRequest;
import com.giaidev.identity.web.response.RoleResponse;
import com.giaidev.identity.domain.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}