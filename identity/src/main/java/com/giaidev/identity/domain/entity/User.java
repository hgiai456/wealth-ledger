package com.giaidev.identity.domain.entity;

import com.giaidev.core.entity.BaseEntity;
import com.giaidev.identity.domain.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users",
        schema = "identity",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_username",
                        columnNames = "username"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {


    @Column(
            name = "username",
            nullable = false,
            length = 255
    )
    private String username;

    @Column(
            name = "email",
            nullable = false,
            length = 320
    )
    private String email;

    @Column(
            name = "password",
            nullable = false,
            length = 100
    )
    private String password;

    @Column(
            name = "first_name",
            length = 100
    )
    private String firstName;

    @Column(
            name = "last_name",
            length = 100
    )
    private String lastName;

    @Column(name = "dob")
    private LocalDate dob;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 100
    )
    private UserStatus status = UserStatus.PENDING;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            schema = "identity",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(
                            name = "fk_user_roles_user"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_name",
                    referencedColumnName = "name",
                    foreignKey = @ForeignKey(
                            name = "fk_user_roles_role"
                    )
            )
    )
    private Set<Role> roles = new HashSet<>();
}