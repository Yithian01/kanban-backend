package com.example.spring_boot_jwt_boilerplate.repository;

import com.example.spring_boot_jwt_boilerplate.domain.kanban.KanbanBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KanbanBoardRepository extends JpaRepository<KanbanBoard, Long> {
}
