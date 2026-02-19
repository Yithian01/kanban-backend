package com.example.spring_boot_jwt_boilerplate.dto.kanban.response;

import com.example.spring_boot_jwt_boilerplate.domain.task.KanbanTask;
import lombok.Getter;

@Getter
public class TaskResponse {
    private Long taskId;
    private String title;
    private String content;
    private Double position;

    public TaskResponse(KanbanTask task) {
        this.taskId = task.getId();
        this.title = task.getTitle();
        this.content = task.getContent();
        this.position = task.getPosition();
    }
}