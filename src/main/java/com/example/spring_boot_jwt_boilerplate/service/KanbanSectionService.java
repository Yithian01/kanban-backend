package com.example.spring_boot_jwt_boilerplate.service;

import com.example.spring_boot_jwt_boilerplate.domain.kanban.KanbanBoard;
import com.example.spring_boot_jwt_boilerplate.domain.member.Member;
import com.example.spring_boot_jwt_boilerplate.domain.section.KanbanSection;
import com.example.spring_boot_jwt_boilerplate.exception.CustomException;
import com.example.spring_boot_jwt_boilerplate.exception.ErrorCode;
import com.example.spring_boot_jwt_boilerplate.repository.KanbanBoardRepository;
import com.example.spring_boot_jwt_boilerplate.repository.KanbanSectionRepository;
import com.example.spring_boot_jwt_boilerplate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KanbanSectionService {

    private final KanbanBoardRepository kanbanBoardRepository;
    private final KanbanSectionRepository kanbanSectionRepository;
    private final MemberRepository memberRepository;

    /**
     * 새로운 칸반 보드가 생성될 때, 기본 섹션 3개(To Do, Doing, Done)를 자동 생성합니다.
     * 보드 생성 로직(`KanbanBoardService`) 내에서 호출됩니다.
     * @param kanbanBoard 섹션이 추가될 칸반 보드 엔티티
     */
    @Transactional
    public void createDefaultSections(KanbanBoard kanbanBoard) {
        List<String> defaultSectionNames = List.of("To Do", "Doing", "Done");

        for (int i = 0; i < defaultSectionNames.size(); i++) {
            KanbanSection section = KanbanSection.builder()
                    .name(defaultSectionNames.get(i))
                    .position((double) i * 1000)
                    .kanbanBoard(kanbanBoard)
                    .build();

            kanbanSectionRepository.save(section);
        }
    }

    /**
     * 새로운 섹션을 수동으로 추가합니다. (사용자 검증 포함)
     */
    @Transactional
    public void addSection(Long boardId, String name, String userEmail) {
        KanbanBoard kanbanBoard = kanbanBoardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!kanbanBoard.getMember().getEmail().equals(userEmail)) {
             throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        KanbanSection section = KanbanSection.builder()
                .name(name)
                .position(nextPosition(kanbanBoard.getId()))
                .kanbanBoard(kanbanBoard)
                .build();
        kanbanSectionRepository.save(section);
    }

    /**
     * 섹션의 이름을 변경합니다.
     * @param boardId 변경할 섹션이 있는 칸반 보드
     * @param sectionId 변경할 섹션 ID
     * @param userEmail 요청이 들어온 멤버
     * @param newName 변경할 섹션 이름
     */
    @Transactional
    public void updateSectionName(Long boardId, Long sectionId, String userEmail, String newName) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        KanbanBoard board = kanbanBoardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        KanbanSection section = kanbanSectionRepository.findById(sectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SECTION_NOT_FOUND));

        if (!section.getKanbanBoard().getId().equals(board.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        section.updateName(newName);
    }

    /**
     * 추가될 섹션의 위치를 반환합니다.
     * @param kanbanId 섹션이 추가될 보드 ID
     * @return 해당 칸반에 존재하는 섹션의 최대 + 1000 반환합니다.
     */
    private Double nextPosition(Long kanbanId) {
        Double maxPosition = kanbanSectionRepository.findMaxPositionByKanbanId(kanbanId);

        return (maxPosition == null) ? 1000.0 : maxPosition + 1000.0;
    }

    /**
     * 보드 내에 존재하는 모든 세션을 반환합니다.
     * @param kanbanId 가져올 섹션이 존재하는 보드 ID
     * @return 해당 칸반에 존재하는 섹션을 반환합니다.
     */
    public List<KanbanSection> findByKanbanIdOrderByPositionAsc(Long kanbanId) {
        return kanbanSectionRepository.findByKanbanBoardIdWithKanbanBoard(kanbanId);
    }

    /**
     * 해당 칸반 ID를 가진 모든 섹션 삭제
     * @param kanbanId 칸반 ID
     */
    public void deleteByKanbanId(Long kanbanId) {
        kanbanSectionRepository.deleteByKanbanId(kanbanId);
    }

    /**
     * 해당 칸반 삭제
     * @param boardId 칸반 ID
     * @param sectionId 삭제할 섹션 ID
     * @param userEmail 유저 확인
     */
    @Transactional
    public void deleteSection(Long boardId, Long sectionId, String userEmail) {
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        KanbanBoard board = kanbanBoardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        KanbanSection section = kanbanSectionRepository.findById(sectionId)
                .orElseThrow(() -> new CustomException(ErrorCode.SECTION_NOT_FOUND));

        if (!section.getKanbanBoard().getId().equals(board.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        kanbanSectionRepository.delete(section);
    }

    @Transactional
    public void updateSectionPosition(Long boardId, Long sectionId, String userEmail, Long targetIndex) {

        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        KanbanBoard board = kanbanBoardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }

        List<KanbanSection> sections = kanbanSectionRepository.findByKanbanBoardIdWithKanbanBoard(boardId);

        KanbanSection movingSection = sections.stream()
                .filter(s -> s.getId().equals(sectionId))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.SECTION_NOT_FOUND));

        sections.remove(movingSection);

        int targetIdx = Math.max(0, Math.min(targetIndex.intValue(), sections.size()));
        sections.add(targetIdx, movingSection);

        for (int i = 0; i < sections.size(); i++) {
            sections.get(i).updatePosition((double) (i + 1) * 1000);
        }
    }
}