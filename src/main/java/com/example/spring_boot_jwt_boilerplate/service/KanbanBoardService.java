package com.example.spring_boot_jwt_boilerplate.service;

import com.example.spring_boot_jwt_boilerplate.domain.kanban.KanbanBoard;
import com.example.spring_boot_jwt_boilerplate.domain.member.Member;
import com.example.spring_boot_jwt_boilerplate.domain.section.KanbanSection;
import com.example.spring_boot_jwt_boilerplate.domain.task.KanbanTask;
import com.example.spring_boot_jwt_boilerplate.dto.kanban.response.BoardDetailResponse;
import com.example.spring_boot_jwt_boilerplate.dto.kanban.response.BoardListResponse;
import com.example.spring_boot_jwt_boilerplate.dto.kanban.response.SectionResponse;
import com.example.spring_boot_jwt_boilerplate.dto.kanban.response.TaskResponse;
import com.example.spring_boot_jwt_boilerplate.exception.CustomException;
import com.example.spring_boot_jwt_boilerplate.exception.ErrorCode;
import com.example.spring_boot_jwt_boilerplate.repository.KanbanBoardRepository;
import com.example.spring_boot_jwt_boilerplate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KanbanBoardService {

    private final MemberRepository memberRepository;
    private final KanbanBoardRepository kanbanBoardRepository;
    private final KanbanSectionService kanbanSectionService;
    private final KanbanTaskService kanbanTaskService;

    /**
     * 새로운 칸반 보드를 생성합니다.
     * @param title 보드 제목
     * @param email 보드를 생성하는 사용자 email
     * @return 생성된 보드 ID
     */
    @Transactional
    public Long createBoard(String title, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        KanbanBoard kanbanBoard = KanbanBoard.builder()
                .title(title)
                .member(member)
                .build();
        kanbanBoardRepository.save(kanbanBoard);

        kanbanSectionService.createDefaultSections(kanbanBoard);
        return kanbanBoard.getId();
    }

    /**
     * 사용자 ID로 보드 목록을 조회합니다.
     * @param userEmail AT --> email --> memberId
     * @return List<BoardListResponse> or empty List
     */
    public List<BoardListResponse> getMyBoards(String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<KanbanBoard> boards = kanbanBoardRepository.findByMemberIdWithMember(member.getId());

        return boards.stream()
                .map(BoardListResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 보드 상세 조회 (섹션 및 태스크 포함)
     */
    public BoardDetailResponse getBoardDetail(Long boardId, String userEmail) {
        KanbanBoard board = kanbanBoardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getMember().getEmail().equals(userEmail)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        List<KanbanSection> sections = kanbanSectionService.findByKanbanIdOrderByPositionAsc(boardId);
        List<KanbanTask> tasks = kanbanTaskService.findByKanbanIdOrderByPositionAsc(boardId);

        List<SectionResponse> sectionResponses = sections.stream()
                .map(section -> {
                    SectionResponse sectionRes = new SectionResponse(section);

                    List<TaskResponse> tasksInSection = tasks.stream()
                            .filter(task -> task.getKanbanSection().getId().equals(section.getId()))
                            .map(TaskResponse::new)
                            .collect(Collectors.toList());
                    sectionRes.setTasks(tasksInSection);
                    return sectionRes;
                })
                .collect(Collectors.toList());

        return new BoardDetailResponse(board, sectionResponses);
    }

    /**
     * 보드 삭제 (섹션 및 태스크 포함)
     */
    @Transactional
    public void deleteBoard(Long boardId, String userEmail) {
        KanbanBoard board = kanbanBoardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getMember().getEmail().equals(userEmail)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        kanbanTaskService.deleteByKanbanId(boardId);

        kanbanSectionService.deleteByKanbanId(boardId);

        kanbanBoardRepository.delete(board);
    }
}