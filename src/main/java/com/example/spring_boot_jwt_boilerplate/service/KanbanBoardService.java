package com.example.spring_boot_jwt_boilerplate.service;

import com.example.spring_boot_jwt_boilerplate.domain.kanban.KanbanBoard;
import com.example.spring_boot_jwt_boilerplate.domain.member.Member;
import com.example.spring_boot_jwt_boilerplate.repository.KanbanBoardRepository;
import com.example.spring_boot_jwt_boilerplate.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본적으로 읽기 전용
public class KanbanBoardService {

    private final KanbanBoardRepository kanbanBoardRepository;
    private final MemberRepository memberRepository;

    /**
     * 새로운 칸반 보드를 생성합니다.
     * @param title 보드 제목
     * @param email 보드를 생성하는 사용자 email
     * @return 생성된 보드 ID
     */
    @Transactional
    public Long createBoard(String title, String email) {
        Member member = memberRepository.findByEmail(email).orElseGet(null);

        KanbanBoard kanbanBoard = KanbanBoard.builder()
                .title(title)
                .member(member)
                .build();
        kanbanBoardRepository.save(kanbanBoard);

        return kanbanBoard.getId();
    }
}