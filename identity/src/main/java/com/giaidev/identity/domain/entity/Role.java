package com.giaidev.identity.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "roles",
        schema = "identity"
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @Column(
            name = "name",
            nullable = false,
            length = 255
    )
    private String name;

    @Column(
            name = "description",
            length = 255
    )
    private String description;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "role_permissions",
            schema = "identity",
            joinColumns = @JoinColumn(
                    name = "role_name",
                    referencedColumnName = "name",
                    foreignKey = @ForeignKey(
                            name = "fk_role_permissions_role"
                    )
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "permission_name",
                    referencedColumnName = "name",
                    foreignKey = @ForeignKey(
                            name = "fk_role_permissions_permission"
                    )
            )
    )
    private Set<Permission> permissions = new HashSet<>();
}