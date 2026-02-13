package com.example.spring_boot_jwt_boilerplate.service;

import com.example.spring_boot_jwt_boilerplate.domain.kanban.KanbanBoard;
import com.example.spring_boot_jwt_boilerplate.domain.section.KanbanSection;
import com.example.spring_boot_jwt_boilerplate.repository.KanbanSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KanbanSectionService {

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
}