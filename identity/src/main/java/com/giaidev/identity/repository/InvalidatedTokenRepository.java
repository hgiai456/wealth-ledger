package com.giaidev.identity.repository;

import com.giaidev.identity.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvalidatedTokenRepository
        extends JpaRepository<
                InvalidatedToken, String> { // Class Repository này giúp tương tác với dữ liệu <Table Name, Type Value>
}
