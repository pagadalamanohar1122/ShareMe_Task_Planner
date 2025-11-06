package com.tasksphere.shareme.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tasksphere.shareme.entity.Project;
import com.tasksphere.shareme.entity.Task;
import com.tasksphere.shareme.entity.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    List<Task> findByProjectOrderByCreatedAtDesc(Project project);
    
    List<Task> findByAssigneeOrderByCreatedAtDesc(User assignee);
    
    List<Task> findByCreatorOrderByCreatedAtDesc(User creator);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId")
    Long countTasksByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    Long countTasksByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") Task.TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignee.id = :userId OR t.creator.id = :userId")
    Long countUserTasks(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE (t.assignee.id = :userId OR t.creator.id = :userId) AND t.status = :status")
    Long countUserTasksByStatus(@Param("userId") Long userId, @Param("status") Task.TaskStatus status);
}