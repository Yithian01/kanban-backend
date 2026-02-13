package com.example.spring_boot_jwt_boilerplate.dto.kanban.response;

import com.example.spring_boot_jwt_boilerplate.domain.section.KanbanSection;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SectionResponse {
    private Long sectionId;
    private String name;
    private Double position;
    private List<TaskResponse> tasks = new ArrayList<>();

    public SectionResponse(KanbanSection section) {
        this.sectionId = section.getId();
        this.name = section.getName();
        this.position = section.getPosition();
    }

    /**
     * 중첩구조 DTO, service에서 사용
     * @param tasks section에 속한 task list
     */
    public void setTasks(List<TaskResponse> tasks) {
        this.tasks = tasks;
    }
}