package com.example.spring_boot_jwt_boilerplate.repository;

import com.example.spring_boot_jwt_boilerplate.domain.section.KanbanSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KanbanSectionRepository extends JpaRepository<KanbanSection, Integer> {
}
