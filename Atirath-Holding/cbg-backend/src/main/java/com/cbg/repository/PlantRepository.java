package com.cbg.repository;

import com.cbg.entity.PlantMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlantRepository
        extends JpaRepository<PlantMaster, Long> {
}