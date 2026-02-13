package com.example.spring_boot_jwt_boilerplate.service;

import com.example.spring_boot_jwt_boilerplate.domain.kanban.KanbanBoard;
import com.example.spring_boot_jwt_boilerplate.domain.member.Member;
import com.example.spring_boot_jwt_boilerplate.domain.section.KanbanSection;
import com.example.spring_boot_jwt_boilerplate.domain.task.KanbanTask;
import com.example.spring_boot_jwt_boilerplate.exception.CustomException;
import com.example.spring_boot_jwt_boilerplate.exception.ErrorCode;
import com.example.spring_boot_jwt_boilerplate.repository.KanbanBoardRepository;
import com.example.spring_boot_jwt_boilerplate.repository.KanbanSectionRepository;
import com.example.spring_boot_jwt_boilerplate.repository.KanbanTaskRepository;
import com.example.spring_boot_jwt_boilerplate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KanbanTaskService {

    private final KanbanTaskRepository kanbanTaskRepository;
    private final KanbanSectionRepository kanbanSectionRepository;
    private final MemberRepository memberRepository;
    private final KanbanBoardRepository kanbanBoardRepository;

    /**
     * 특정 섹션에 새로운 태스크를 생성합니다.
     * @param kanbanBoardId 생성할 보드 ID
     * @param sectionId 생성할 섹션 ID
     * @param memberId 멤버 ID
     * @param title 태스크명
     * @param content 태스크 설명
     * @return 반환 여부
     */
    @Transactional
    public Long createTask(Long kanbanBoardId, Long sectionId, Integer memberId, String title, String content) {
        kanbanBoardRepository.findById(kanbanBoardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        KanbanSection section = kanbanSectionRepository.findById(sectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SECTION_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        KanbanTask task = KanbanTask.builder()
                .title(title)
                .content(content)
                .kanbanSection(section)
                .kanbanId(kanbanBoardId)
                .member(member)
                .position(nextTaskPosition(sectionId))
                .build();

        return kanbanTaskRepository.save(task).getId();
    }

    /**
     * 추가될 태스크의 위치를 반환합니다.
     * @param sectionId 태스크가 추가될 섹션 ID
     * @return 해당 섹션에 존재하는 태스크의 최대 + 1000 반환합니다.
     */
    private Double nextTaskPosition(Long sectionId) {
        Double maxPosition = kanbanTaskRepository.findMaxPositionBySectionId(sectionId);
        return (maxPosition == null) ? 1000.0 : maxPosition + 1000.0;
    }

    /**
     * 상세 조회용 섹션 + 태스크 정보 같이 반환합니다.
     * @param boardId 보드 ID
     * @return 섹션 + 태스크 정보를 같이 반환합니다.
     */
    public List<KanbanTask> findByKanbanIdOrderByPositionAsc(Long boardId) {
        return kanbanTaskRepository.findByKanbanIdOrderByPositionAsc(boardId);
    }
}