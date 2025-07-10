package com.bika.company.entity;

import com.bika.core.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "companies")
public class Company extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String email;

    @Column
    private String phone;

    @Column
    private String address;

    @Column
    private String description;

    @Builder.Default
    @Column(name = "is_active")
    private boolean active = true;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("company")
    private List<Department> departments = new ArrayList<>();
} 