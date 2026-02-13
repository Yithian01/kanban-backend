package com.example.spring_boot_jwt_boilerplate.repository;

import com.example.spring_boot_jwt_boilerplate.domain.task.KanbanTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KanbanTaskRepository extends JpaRepository<KanbanTask, Long> {
}
