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
import org.springframework.data.redis.stream.Task;
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
     * @param boardId 생성할 보드 ID
     * @param sectionId 생성할 섹션 ID
     * @param userEmail 멤버 email
     * @param title 태스크명
     * @param content 태스크 설명
     */
    @Transactional
    public void createTask(Long boardId, Long sectionId, String userEmail, String title, String content) {
        KanbanBoard board = kanbanBoardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        KanbanSection section = kanbanSectionRepository.findById(sectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SECTION_NOT_FOUND));

        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        KanbanTask task = KanbanTask.builder()
                .title(title)
                .content(content)
                .kanbanSection(section)
                .kanbanId(board.getId())
                .member(member)
                .position(nextTaskPosition(sectionId))
                .build();

       kanbanTaskRepository.save(task);
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

    /**
     * 해당 되는 보드 내부의 모든 태스크를 삭제합니다.
     * @param boardId 삭제할 보드 ID
     */
    public void deleteByKanbanId(Long boardId) {
        kanbanTaskRepository.deleteByKanbanId(boardId);
    }

    /**
     * 해당 태스크 카드 삭제
     * @param boardId 어떤 보드
     * @param sectionId 어떤 섹션
     * @param taskId 어떤 태스크
     * @param userEmail 어떤 유저
     */
    @Transactional
    public void deleteTask(Long boardId, Long sectionId, Long taskId, String userEmail) {
        kanbanBoardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        KanbanSection section = kanbanSectionRepository.findById(sectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SECTION_NOT_FOUND));

        if (!section.getKanbanBoard().getId().equals(boardId)) {
            throw new CustomException(ErrorCode.INVALID_SECTION_LOCATION);
        }

        KanbanTask task = kanbanTaskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        if (!task.getKanbanSection().getId().equals(sectionId)) {
            throw new CustomException(ErrorCode.INVALID_TASK_LOCATION);
        }

        kanbanTaskRepository.delete(task);
    }

    /**
     * 특정 태스크를 수정합니다.
     * @param boardId 수정할 보드 ID
     * @param sectionId 수정할 섹션 ID
     * @param userEmail 멤버 email
     * @param title 수정할 태스크명
     * @param content 수정할 태스크 설명
     */
    @Transactional
    public void updateSectionName(Long boardId, Long sectionId,  Long taskId, String userEmail, String title, String content) {
        kanbanBoardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        KanbanSection section = kanbanSectionRepository.findById(sectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SECTION_NOT_FOUND));

        if (!section.getKanbanBoard().getId().equals(boardId)) {
            throw new CustomException(ErrorCode.INVALID_SECTION_LOCATION);
        }

        KanbanTask task = kanbanTaskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        if (!task.getKanbanSection().getId().equals(sectionId)) {
            throw new CustomException(ErrorCode.INVALID_TASK_LOCATION);
        }

        task.update(title, content);
    }

    /**
     * 태스크를 섹션과 순서를 재조정
     * @param boardId 어떤 보드의
     * @param taskId 어떤 태스크를
     * @param targetSectionId 어떤 섹션의
     * @param targetIndex 어떤 순서로
     * @param userEmail 멤버 email
     */
    @Transactional
    public void moveTask(Long boardId, Long taskId, Long targetSectionId, Long targetIndex, String userEmail) {
        kanbanBoardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        KanbanTask task = kanbanTaskRepository.findById(taskId)
                .orElseThrow(() -> new CustomException(ErrorCode.TASK_NOT_FOUND));

        KanbanSection targetSection = kanbanSectionRepository.findById(targetSectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SECTION_NOT_FOUND));

        if (!targetSection.getKanbanBoard().getId().equals(boardId)) {
            throw new CustomException(ErrorCode.INVALID_SECTION_LOCATION);
        }
        if (!targetSection.getKanbanBoard().getMember().equals(member)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        if (!task.getKanbanSection().getId().equals(targetSectionId)) {
            task.updateSection(targetSection);
        }

        List<KanbanTask> tasksInTargetSection = kanbanTaskRepository
                .findByKanbanSectionIdWithSection(targetSectionId);

        tasksInTargetSection.remove(task);
        int targetIdx = Math.max(0, Math.min(targetIndex.intValue(), tasksInTargetSection.size()));
        tasksInTargetSection.add(targetIdx, task);

        for (int i = 0; i < tasksInTargetSection.size(); i++) {
            tasksInTargetSection.get(i).updatePosition((double) (i + 1) * 1000);
        }
    }
}