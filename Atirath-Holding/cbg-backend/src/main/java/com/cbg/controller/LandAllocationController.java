package com.cbg.controller;

import com.cbg.entity.LandAllocation;
import com.cbg.repository.LandAllocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class LandAllocationController {

    @Autowired
    private LandAllocationRepository landAllocationRepository;

    @GetMapping("/land-allocations")
    public List<LandAllocation> getLandAllocations() {
        return landAllocationRepository.findAll();
    }

    @PostMapping("/land-allocations")
    public ResponseEntity<LandAllocation> saveLandAllocation(@RequestBody LandAllocation allocation) {
        LandAllocation saved = landAllocationRepository.save(allocation);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/land-allocations/{id}")
    public ResponseEntity<Void> deleteLandAllocation(@PathVariable Long id) {
        landAllocationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
