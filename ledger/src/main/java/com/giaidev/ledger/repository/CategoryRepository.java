package com.giaidev.ledger.repository;

import com.giaidev.ledger.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, String> {
    @Query("""
        select category
        from Category category
        where category.id = :categoryId
          and category.active = true
          and (
              category.userId = :userId
              or category.userId is null
          )
        """)
    Optional<Category> findUsableById(
            @Param("categoryId") String categoryId,
            @Param("userId") String userId
    );
}
