package com.example.spring_boot_jwt_boilerplate.dto.kanban.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KanbanBoardRenameRequest {
    private String title;
}