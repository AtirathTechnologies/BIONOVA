package com.cbg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "department_master")
@Getter
@Setter
public class DepartmentMaster {

    @Id
    @Column(name = "dept_id")
    private Long deptId;

    @Column(name = "dept_nm")
    private String deptNm;
}