package com.example.spring_boot_jwt_boilerplate.dto.kanban.response;

import com.example.spring_boot_jwt_boilerplate.domain.kanban.KanbanBoard;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class BoardListResponse {

    private Long boardId;
    private String title;
    private String creatorEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    public BoardListResponse(KanbanBoard kanbanBoard) {
        this.boardId = kanbanBoard.getId();
        this.title = kanbanBoard.getTitle();
        this.creatorEmail = kanbanBoard.getMember().getEmail();
        this.createdAt = kanbanBoard.getCreateAt();
        this.updateAt = kanbanBoard.getUpdateAt();
    }
}