package com.cbg.repository;

import com.cbg.entity.DepartmentMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository
        extends JpaRepository<DepartmentMaster, Long> {
}