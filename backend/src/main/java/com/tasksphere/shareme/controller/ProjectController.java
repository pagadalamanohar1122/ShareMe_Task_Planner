package com.tasksphere.shareme.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tasksphere.shareme.dto.CreateProjectRequest;
import com.tasksphere.shareme.dto.ErrorResponse;
import com.tasksphere.shareme.dto.ProjectResponse;
import com.tasksphere.shareme.security.JwtTokenProvider;
import com.tasksphere.shareme.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
@Tag(name = "Project Management", description = "Comprehensive project management APIs for TaskSphere - Create, read, update, and delete projects with advanced analytics")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping
    @Operation(summary = "Get User Projects", description = "Retrieve all projects belonging to the authenticated user with complete project details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<ProjectResponse>> getUserProjects(
            @Parameter(description = "JWT Bearer token for authentication", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

            List<ProjectResponse> projects = projectService.getUserProjects(userId);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @Operation(summary = "Create New Project", description = "Create a new project for the authenticated user with specified details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Project created successfully",
                content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid project data",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectResponse> createProject(
            @Parameter(description = "Project creation details", required = true)
            @Valid @RequestBody CreateProjectRequest request,
            @Parameter(description = "JWT Bearer token for authentication", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

            ProjectResponse project = projectService.createProject(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(project);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Project by ID", description = "Retrieve a specific project by its ID with full details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Project retrieved successfully",
                content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - Access denied to this project",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Project not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectResponse> getProject(
            @Parameter(description = "Project ID to retrieve", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "JWT Bearer token for authentication", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

            ProjectResponse project = projectService.getProjectById(id, userId);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().equals("Access denied")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Project", description = "Update an existing project (only project owner can update)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Project updated successfully",
                content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid project data",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Forbidden - Only project owner can update",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Project not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ProjectResponse> updateProject(
            @Parameter(description = "Project ID to update", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated project details", required = true)
            @Valid @RequestBody CreateProjectRequest request,
            @Parameter(description = "JWT Bearer token for authentication", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

            ProjectResponse project = projectService.updateProject(id, request, userId);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Only project owner")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Project", description = "Delete a project permanently (only project owner can delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Only project owner can delete",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Project not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteProject(
            @Parameter(description = "Project ID to delete", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "JWT Bearer token for authentication", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

            projectService.deleteProject(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().equals("Project not found")) {
                return ResponseEntity.notFound().build();
            } else if (e.getMessage().contains("Only project owner")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stats")
    @Operation(summary = "Get User Statistics", description = "Retrieve comprehensive statistics about user's projects and tasks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Map<String, Object>> getUserStats(
            @Parameter(description = "JWT Bearer token for authentication", required = true, example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            @RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            Long userId = jwtTokenProvider.getUserIdFromToken(jwt);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalProjects", projectService.getProjectCount(userId));
            stats.put("completedTasks", projectService.getCompletedTasksCount(userId));
            stats.put("inProgressTasks", projectService.getInProgressTasksCount(userId));

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}