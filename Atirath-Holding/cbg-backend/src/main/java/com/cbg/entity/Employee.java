package com.cbg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "employee_master")
@SecondaryTable(
    name = "employee_password_master",
    pkJoinColumns = @PrimaryKeyJoinColumn(name = "emp_id", referencedColumnName = "emp_id")
)
@Getter
@Setter
public class Employee {

    @Id
    @Column(name = "emp_id")
    private Long empId;

    @Column(name = "email")
    private String email;

    @Column(name = "emp_password", table = "employee_password_master")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "sts")
    private Boolean status;
}