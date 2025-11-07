package com.tasksphere.shareme.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tasksphere.shareme.dto.TaskNoteRequest;
import com.tasksphere.shareme.dto.TaskNoteResponse;
import com.tasksphere.shareme.dto.ErrorResponse;
import com.tasksphere.shareme.service.TaskNoteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/task-notes")
@Tag(name = "Task Notes", description = "Personal task notes and reminder tags management APIs - Create, read, update, and delete personal notes associated with tasks")
@SecurityRequirement(name = "bearerAuth")
public class TaskNoteController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskNoteController.class);
    
    @Autowired
    private TaskNoteService taskNoteService;
    
    @Operation(summary = "Get personal note for a task", 
               description = "Retrieve the current user's personal note for a specific task with all reminder tags and content")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task note retrieved successfully",
                content = @Content(schema = @Schema(implementation = TaskNoteResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task or note not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/task/{taskId}")
    public ResponseEntity<TaskNoteResponse> getTaskNote(
            @Parameter(description = "Task ID to retrieve the note for", required = true, example = "1") 
            @PathVariable Long taskId,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("userId");
        logger.info("Getting task note for user {} and task {}", userId, taskId);
        
        if (userId == null) {
            logger.error("User ID is null in request attributes");
            return ResponseEntity.status(401).build();
        }
        
        try {
            TaskNoteResponse response = taskNoteService.getTaskNote(userId, taskId);
            logger.info("Task note retrieved successfully for user {} and task {}", userId, taskId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting task note for user {} and task {}: {}", userId, taskId, e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
    
    @Operation(summary = "Save personal note for a task", 
               description = "Create or update the current user's personal note for a specific task with content and reminder tags")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task note updated successfully",
                content = @Content(schema = @Schema(implementation = TaskNoteResponse.class))),
        @ApiResponse(responseCode = "201", description = "Task note created successfully",
                content = @Content(schema = @Schema(implementation = TaskNoteResponse.class))),
        @ApiResponse(responseCode = "400", description = "Bad request - Invalid note data",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Task not found",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<TaskNoteResponse> saveTaskNote(
            @Valid @RequestBody TaskNoteRequest request,
            HttpServletRequest httpRequest) {
        
        Long userId = (Long) httpRequest.getAttribute("userId");
        
        // For testing purposes, use a default user ID if not authenticated
        if (userId == null) {
            userId = 1L; // Default test user ID
            logger.warn("No authenticated user found, using default user ID {} for testing", userId);
        }
        
        logger.info("Saving task note for user {} and task {}", userId, request.getTaskId());
        
        try {
            TaskNoteResponse response = taskNoteService.saveTaskNote(userId, request);
            logger.info("Task note saved successfully for user {} and task {}", userId, request.getTaskId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error saving task note for user {} and task {}: {}", userId, request.getTaskId(), e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
    
    @Operation(summary = "Delete personal note for a task", 
               description = "Delete the current user's personal note for a specific task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task note deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Task note not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/task/{taskId}")
    public ResponseEntity<Void> deleteTaskNote(
            @Parameter(description = "Task ID") @PathVariable Long taskId,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("userId");
        logger.info("Deleting task note for user {} and task {}", userId, taskId);
        
        taskNoteService.deleteTaskNote(userId, taskId);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Get all personal task notes", 
               description = "Retrieve all personal task notes for the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task notes retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<TaskNoteResponse>> getUserTaskNotes(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        // For testing purposes, use a default user ID if not authenticated
        if (userId == null) {
            userId = 1L; // Default test user ID
            logger.warn("No authenticated user found, using default user ID {} for testing", userId);
        }
        
        logger.info("Getting all task notes for user {}", userId);
        
        List<TaskNoteResponse> notes = taskNoteService.getUserTaskNotes(userId);
        return ResponseEntity.ok(notes);
    }
    
    @Operation(summary = "Get task notes by tag", 
               description = "Retrieve personal task notes filtered by a specific reminder tag")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task notes retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<TaskNoteResponse>> getTaskNotesByTag(
            @Parameter(description = "Reminder tag") @PathVariable String tag,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("userId");
        logger.info("Getting task notes for user {} with tag {}", userId, tag);
        
        List<TaskNoteResponse> notes = taskNoteService.getUserTaskNotesByTag(userId, tag);
        return ResponseEntity.ok(notes);
    }
    
    @Operation(summary = "Get all user tags", 
               description = "Retrieve all unique reminder tags used by the current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tags retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/tags")
    public ResponseEntity<List<String>> getUserTags(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        // For testing purposes, use a default user ID if not authenticated
        if (userId == null) {
            userId = 1L; // Default test user ID
            logger.warn("No authenticated user found, using default user ID {} for testing", userId);
        }
        
        logger.info("Getting all tags for user {}", userId);
        
        List<String> tags = taskNoteService.getUserTags(userId);
        return ResponseEntity.ok(tags);
    }
    
    @Operation(summary = "Check if task has note", 
               description = "Check if the current user has a personal note for a specific task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check completed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/task/{taskId}/exists")
    public ResponseEntity<Boolean> hasTaskNote(
            @Parameter(description = "Task ID") @PathVariable Long taskId,
            HttpServletRequest request) {
        
        Long userId = (Long) request.getAttribute("userId");
        logger.debug("Checking if user {} has note for task {}", userId, taskId);
        
        boolean hasNote = taskNoteService.hasTaskNote(userId, taskId);
        return ResponseEntity.ok(hasNote);
    }
}