package com.cbg.controller;

import com.cbg.entity.DepartmentMaster;
import com.cbg.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DepartmentController {

    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping("/departments")
    public List<DepartmentMaster> getDepartments() {
        return departmentRepository.findAll();
    }
}