package com.example.spring_boot_jwt_boilerplate.service;

import com.example.spring_boot_jwt_boilerplate.domain.kanban.KanbanBoard;
import com.example.spring_boot_jwt_boilerplate.domain.section.KanbanSection;
import com.example.spring_boot_jwt_boilerplate.exception.CustomException;
import com.example.spring_boot_jwt_boilerplate.exception.ErrorCode;
import com.example.spring_boot_jwt_boilerplate.repository.KanbanBoardRepository;
import com.example.spring_boot_jwt_boilerplate.repository.KanbanSectionRepository;
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
     * 새로운 섹션을 수동으로 추가합니다.
     * @param kanbanBoardId 대상 보드 ID
     * @param name 섹션 이름
     */
    @Transactional
    public Long addSection(Long kanbanBoardId, String name) {
        KanbanBoard kanbanBoard = kanbanBoardRepository.findById(kanbanBoardId)
                .orElseThrow(() ->  new CustomException(ErrorCode.BOARD_NOT_FOUND));

        KanbanSection section = KanbanSection.builder()
                .name(name)
                .position(nextPosition(kanbanBoard.getId()))
                .kanbanBoard(kanbanBoard)
                .build();

        return kanbanSectionRepository.save(section).getId();
    }

    /**
     * 섹션의 이름을 변경합니다.
     * @param sectionId 변경할 섹션 ID
     * @param newName 변경할 섹션명
     */
    @Transactional
    public void updateSectionName(Long sectionId, String newName) {
        KanbanSection section = kanbanSectionRepository.findById(sectionId)
                .orElseThrow(() ->  new CustomException(ErrorCode.SECTION_NOT_FOUND));

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
}