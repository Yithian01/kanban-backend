package com.example.spring_boot_jwt_boilerplate.repository;

import com.example.spring_boot_jwt_boilerplate.domain.section.KanbanSection;
import com.example.spring_boot_jwt_boilerplate.domain.task.KanbanTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    /**
     * 특정 섹션 내의 태스크 중 가장 큰 position 값을 조회합니다.
     * @param sectionId 섹션 ID
     * @return 최대 position 값을 반환합니다.
     */
    @Query("SELECT MAX(kt.position) " +
            "FROM KanbanTask kt " +
            "WHERE kt.kanbanSection.id = :sectionId")
    Double findMaxPositionBySectionId(@Param("sectionId") Long sectionId);

    /**
     * 상세 조회용: 보드에 속한 모든 섹션을 위치순으로 조회
     * @param boardId 보드 ID
     * @return 보드에 속한 task list 반환합니다.
     */
    @Query("SELECT t FROM KanbanTask t " +
            "JOIN FETCH t.kanbanSection s " +
            "WHERE t.kanbanId = :boardId " +
            "ORDER BY t.position ASC")
    List<KanbanTask> findByKanbanIdOrderByPositionAsc(@Param("boardId") Long boardId);

    /**
     * 해당 칸반에 속해있는 태스크 삭제
     * @param kanbanId 삭제할 칸반
     */
    @Modifying
    @Query("DELETE FROM KanbanTask t WHERE t.kanbanId = :kanbanId")
    void deleteByKanbanId(@Param("kanbanId") Long kanbanId);
}