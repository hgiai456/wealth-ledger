package com.giaidev.identity.infrastructure.repository;

import com.giaidev.identity.domain.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository
        extends JpaRepository<
                InvalidatedToken, String> { // Class Repository này giúp tương tác với dữ liệu <Table Name, Type Value>
}
