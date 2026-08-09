package com.giaidev.identity.repository;

import com.giaidev.identity.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository
        extends JpaRepository<
                Permission, String> { // Class Repository này giúp tương tác với dữ liệu <Table Name, Type Value>
}
