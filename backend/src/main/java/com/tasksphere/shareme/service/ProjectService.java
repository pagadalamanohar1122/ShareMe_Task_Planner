package com.tasksphere.shareme.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tasksphere.shareme.dto.CreateProjectRequest;
import com.tasksphere.shareme.dto.ProjectResponse;
import com.tasksphere.shareme.dto.UserInfo;
import com.tasksphere.shareme.entity.Project;
import com.tasksphere.shareme.entity.Task;
import com.tasksphere.shareme.entity.User;
import com.tasksphere.shareme.repository.ProjectRepository;
import com.tasksphere.shareme.repository.TaskRepository;
import com.tasksphere.shareme.repository.UserRepository;

@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ProjectResponse> getUserProjects(Long userId) {
        List<Project> projects = projectRepository.findAllUserProjects(userId);
        return projects.stream()
                .map(this::convertToProjectResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse createProject(CreateProjectRequest request, Long ownerId) {
        Optional<User> ownerOpt = userRepository.findById(ownerId);
        if (ownerOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User owner = ownerOpt.get();
        Project project = new Project(request.getName(), request.getDescription(), owner);
        project.setDeadline(request.getDeadline());

        Project savedProject = projectRepository.save(project);
        return convertToProjectResponse(savedProject);
    }

    public ProjectResponse getProjectById(Long projectId, Long userId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            throw new RuntimeException("Project not found");
        }

        Project project = projectOpt.get();
        
        // Check if user has access to this project
        boolean hasAccess = project.getOwner().getId().equals(userId) ||
                project.getMembers().stream().anyMatch(member -> member.getId().equals(userId));
        
        if (!hasAccess) {
            throw new RuntimeException("Access denied");
        }

        return convertToProjectResponse(project);
    }

    public ProjectResponse updateProject(Long projectId, CreateProjectRequest request, Long userId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            throw new RuntimeException("Project not found");
        }

        Project project = projectOpt.get();
        
        // Only owner can update project
        if (!project.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Only project owner can update the project");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setDeadline(request.getDeadline());

        Project savedProject = projectRepository.save(project);
        return convertToProjectResponse(savedProject);
    }

    public void deleteProject(Long projectId, Long userId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isEmpty()) {
            throw new RuntimeException("Project not found");
        }

        Project project = projectOpt.get();
        
        // Only owner can delete project
        if (!project.getOwner().getId().equals(userId)) {
            throw new RuntimeException("Only project owner can delete the project");
        }

        projectRepository.delete(project);
    }

    public Long getProjectCount(Long userId) {
        return projectRepository.countUserProjects(userId);
    }

    public Long getCompletedTasksCount(Long userId) {
        return taskRepository.countUserTasksByStatus(userId, Task.TaskStatus.COMPLETED);
    }

    public Long getInProgressTasksCount(Long userId) {
        return taskRepository.countUserTasksByStatus(userId, Task.TaskStatus.IN_PROGRESS);
    }

    private ProjectResponse convertToProjectResponse(Project project) {
        UserInfo ownerInfo = new UserInfo(
                project.getOwner().getId(),
                project.getOwner().getFirstName(),
                project.getOwner().getLastName(),
                project.getOwner().getEmail(),
                project.getOwner().getRole().toString()
        );

        ProjectResponse response = new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                ownerInfo,
                project.getCreatedAt(),
                project.getDeadline()
        );

        response.setUpdatedAt(project.getUpdatedAt());

        // Set task counts
        Long totalTasks = taskRepository.countTasksByProjectId(project.getId());
        Long completedTasks = taskRepository.countTasksByProjectIdAndStatus(project.getId(), Task.TaskStatus.COMPLETED);
        Long inProgressTasks = taskRepository.countTasksByProjectIdAndStatus(project.getId(), Task.TaskStatus.IN_PROGRESS);

        response.setTotalTasks(totalTasks.intValue());
        response.setCompletedTasks(completedTasks.intValue());
        response.setInProgressTasks(inProgressTasks.intValue());

        // Set members if needed
        if (project.getMembers() != null) {
            List<UserInfo> members = project.getMembers().stream()
                    .map(member -> new UserInfo(
                            member.getId(),
                            member.getFirstName(),
                            member.getLastName(),
                            member.getEmail(),
                            member.getRole().toString()
                    ))
                    .collect(Collectors.toList());
            response.setMembers(members);
        }

        return response;
    }
}