package com.cbg.controller;

import com.cbg.dto.ProjectRequest;
import com.cbg.entity.Project;
import com.cbg.service.ProjectService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    public Project createProject(
            @RequestBody ProjectRequest request){

        return service.save(request);
    }
}