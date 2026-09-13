package com.giaidev.ledger.entity;


import com.giaidev.core.entity.BaseEntity;
import com.giaidev.ledger.enums.CategoryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(
        name = "categories",
        schema = "ledger"
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {
    @Column(
            name = "user_id",
            length = 36
    )
    private String userId;

    @Column(
            name = "parent_id",
            length = 36
    )
    private String parentId;

    @Column(
            name = "name",
            nullable = false,
            length = 120
    )
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "type",
            nullable = false,
            length = 20
    )
    private CategoryType type;

    @Column(
            name = "active",
            nullable = false
    )
    private boolean active;

    private Category(
            String userId,
            String parentId,
            String name,
            CategoryType type
    ) {
        this.userId = requireText(userId, "userId");
        this.parentId = parentId;
        this.name = requireText(name, "name");
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.active = true;
    } //Constructor co tham so

    public static Category createForUser(
            String userId,
            String parentId,
            String name,
            CategoryType type
    ) {
        return new Category(userId, parentId, name, type);
    }

    public void updateDetails(
            String parentId,
            String name
    ) {
        this.parentId = parentId;
        this.name = requireText(name, "name");
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + " must not be blank"
            );
        }

        return value.trim();
    }


}
