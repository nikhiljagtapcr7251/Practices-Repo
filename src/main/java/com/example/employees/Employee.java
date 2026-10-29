package com.example.employees;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Designation designation;

    protected Employee() {
    }

    public Employee(String name, String email, Designation designation) {
        this.name = name;
        this.email = email;
        this.designation = designation;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Designation getDesignation() {
        return designation;
    }

    public void update(String name, String email, Designation designation) {
        this.name = name;
        this.email = email;
        this.designation = designation;
    }
}
