package com.example.spring_boot_jwt_boilerplate.repository;

import com.example.spring_boot_jwt_boilerplate.domain.kanban.KanbanBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KanbanBoardRepository extends JpaRepository<KanbanBoard, Long> {

    /**
     * 특정 사용자가 소유한 모든 칸반 보드 조회
     * @param memberId 사용자 ID
     * @return 해당 사용자의 보드 목록
     */
    @Query("SELECT kb " +
            "FROM KanbanBoard kb " +
            "JOIN FETCH kb.member " +
            "WHERE kb.member.id = :memberId")
    List<KanbanBoard> findByMemberIdWithMember(@Param("memberId") Long memberId);
}