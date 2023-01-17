package com.example.corespringsecurity.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class ResourcesRole {

    @Id  @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resouces_id")
    @OrderBy("orderNum")
    private Resources resources;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Builder
    public ResourcesRole(Resources resources, Role role) {
        this.resources = resources;
        this.role = role;
    }
}
