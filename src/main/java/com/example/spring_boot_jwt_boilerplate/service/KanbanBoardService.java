package com.example.spring_boot_jwt_boilerplate.service;

import com.example.spring_boot_jwt_boilerplate.domain.kanban.KanbanBoard;
import com.example.spring_boot_jwt_boilerplate.domain.member.Member;
import com.example.spring_boot_jwt_boilerplate.exception.CustomException;
import com.example.spring_boot_jwt_boilerplate.exception.ErrorCode;
import com.example.spring_boot_jwt_boilerplate.repository.KanbanBoardRepository;
import com.example.spring_boot_jwt_boilerplate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KanbanBoardService {

    private final MemberRepository memberRepository;
    private final KanbanBoardRepository kanbanBoardRepository;
    private final KanbanSectionService kanbanSectionService;

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
}