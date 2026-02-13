package com.example.spring_boot_jwt_boilerplate.repository;

import com.example.spring_boot_jwt_boilerplate.domain.section.KanbanSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KanbanSectionRepository extends JpaRepository<KanbanSection, Long> {

    /**
     * 특정 칸반 보드 내의 섹션 반환
     * @param kanbanId 칸반 보드 ID
     * @return 해당 칸반 보드의 섹션 목록
     */
    @Query("SELECT ks " +
            "FROM KanbanSection ks " +
            "JOIN FETCH ks.kanbanBoard " +
            "WHERE ks.kanbanBoard.id = :kanbanId " +
            "ORDER BY ks.position ASC")
    List<KanbanSection> findByKanbanBoardIdWithKanbanBoard(@Param("kanbanId") Long kanbanId);
}
