package com.example.spring_boot_jwt_boilerplate.dto.kanban.response;

import com.example.spring_boot_jwt_boilerplate.domain.kanban.KanbanBoard;
import lombok.Getter;

import java.util.List;

@Getter
public class BoardDetailResponse {
    private Long boardId;
    private String title;
    private List<SectionResponse> sections;

    public BoardDetailResponse(KanbanBoard board, List<SectionResponse> sections) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.sections = sections;
    }
}