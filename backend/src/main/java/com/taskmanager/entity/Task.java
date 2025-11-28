package com.taskmanager.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task extends PanacheEntity {

    public String title;

    @Column(length = 1000)
    public String description;

    @Enumerated(EnumType.STRING)
    public TaskStatus status = TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    public TaskPriority priority = TaskPriority.MEDIUM;

    public LocalDateTime createdAt = LocalDateTime.now();

    public LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum TaskStatus {
        TODO, IN_PROGRESS, DONE
    }

    public enum TaskPriority {
        LOW, MEDIUM, HIGH
    }
}