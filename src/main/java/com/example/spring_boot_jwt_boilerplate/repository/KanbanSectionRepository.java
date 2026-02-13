package com.example.spring_boot_jwt_boilerplate.repository;

import com.example.spring_boot_jwt_boilerplate.domain.section.KanbanSection;
import com.example.spring_boot_jwt_boilerplate.domain.task.KanbanTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KanbanSectionRepository extends JpaRepository<KanbanSection, Long> {

    /**
     * 상세 조회용: 보드에 속한 모든 섹션을 위치순으로 조회
     * @param kanbanId 칸반 보드 ID
     * @return 해당 칸반 보드의 섹션 목록
     */
    @Query("SELECT ks " +
            "FROM KanbanSection ks " +
            "JOIN FETCH ks.kanbanBoard " +
            "WHERE ks.kanbanBoard.id = :kanbanId " +
            "ORDER BY ks.position ASC")
    List<KanbanSection> findByKanbanBoardIdWithKanbanBoard(@Param("kanbanId") Long kanbanId);

    /**
     * 특정 보드 내의 섹션 중 가장 큰 position 값을 조회합니다.
     * @param kanbanId 보드 ID
     * @return 최대 position 값 (섹션이 없으면 null)
     */
    @Query("SELECT MAX(ks.position) " +
            "FROM KanbanSection ks " +
            "WHERE ks.kanbanBoard.id = :kanbanId")
    Double findMaxPositionByKanbanId(@Param("kanbanId") Long kanbanId);
}
