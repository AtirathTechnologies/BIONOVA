package com.cbg.repository;

import com.cbg.entity.LandAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LandAllocationRepository extends JpaRepository<LandAllocation, Long> {
}
