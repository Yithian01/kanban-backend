package com.example.spring_boot_jwt_boilerplate.repository;

import com.example.spring_boot_jwt_boilerplate.domain.task.KanbanTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Import 추가
import org.springframework.data.repository.query.Param; // Import 추가

import java.util.List;

public interface KanbanTaskRepository extends JpaRepository<KanbanTask, Long> {

    /**
     * 보드 전체 화면을 렌더링할 때 태스크, 섹션, 담당자 정보를 한 번에 가져옵니다.
     * @param kanbanId 칸반 보드 ID
     * @return 해당 보드의 태스크 목록 (연관 엔티티 포함)
     */
    @Query("SELECT kt " +
            "FROM KanbanTask kt " +
            "JOIN FETCH kt.kanbanSection " + // 섹션 정보 JOIN FETCH
            "JOIN FETCH kt.member " +         // 멤버 정보 JOIN FETCH
            "WHERE kt.kanbanId = :kanbanId " +
            "ORDER BY kt.position ASC")
    List<KanbanTask> findByKanbanIdWithAllRelations(@Param("kanbanId") Long kanbanId);

    /**
     * 특정 섹션에 속한 모든 태스크 조회 (position 순서대로)
     * 특정 컬럼(예: "To Do")의 카드들만 정렬해서 가져올 때 사용합니다.
     * @param sectionId 섹션 ID
     * @return 해당 섹션의 태스크 목록 (순서 정렬됨)
     */
    @Query("SELECT kt " +
            "FROM KanbanTask kt " +
            "JOIN FETCH kt.kanbanSection " +
            "WHERE kt.kanbanSection.id = :sectionId " +
            "ORDER BY kt.position ASC")
    List<KanbanTask> findByKanbanSectionIdWithSection(@Param("sectionId") Long sectionId);

    /**
     * 보드 화면을 렌더링할 때 태스크와 그 태스크의 섹션 정보를 한 번에 가져옵니다.
     * @param kanbanId 칸반 보드 ID
     * @return 해당 보드의 태스크 목록 (섹션 정보 포함)
     */
    @Query("SELECT kt " +
            "FROM KanbanTask kt " +
            "JOIN FETCH kt.kanbanSection " +
            "WHERE kt.kanbanId = :kanbanId " +
            "ORDER BY kt.position ASC")
    List<KanbanTask> findByKanbanIdWithSection(@Param("kanbanId") Long kanbanId);
}