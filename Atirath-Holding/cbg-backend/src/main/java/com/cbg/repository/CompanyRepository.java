package com.cbg.repository;

import com.cbg.entity.CompanyMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository
        extends JpaRepository<CompanyMaster, Long> {
}