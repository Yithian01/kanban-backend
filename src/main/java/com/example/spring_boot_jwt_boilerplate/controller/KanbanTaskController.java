package com.example.spring_boot_jwt_boilerplate.controller;

import com.example.spring_boot_jwt_boilerplate.dto.common.ApiResponse;
import com.example.spring_boot_jwt_boilerplate.dto.task.CreateTaskRequest;
import com.example.spring_boot_jwt_boilerplate.dto.task.UpdateTaskRequest;
import com.example.spring_boot_jwt_boilerplate.service.KanbanTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban/boards")
public class KanbanTaskController {

    private final KanbanTaskService kanbanTaskService;

    /**
     * 특정 섹션 내에 새로운 태스크(카드) 생성
     * POST /api/kanban/boards/{boardId}/sections/{sectionId}/tasks
     */
    @PostMapping("/{boardId}/sections/{sectionId}/tasks")
    public ResponseEntity<ApiResponse<Void>> createTask(
             @PathVariable("boardId") Long boardId,
             @PathVariable("sectionId") Long sectionId,
             @RequestBody CreateTaskRequest request,
             @AuthenticationPrincipal String userEmail) {

        kanbanTaskService.createTask(boardId, sectionId, userEmail, request.getTitle(), request.getContent());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 특정 태스크 삭제
     * DELETE /api/kanban/boards/{boardId}/sections/{sectionId}/tasks/{taskId}
     */
    @DeleteMapping("/{boardId}/sections/{sectionId}/tasks/{taskId}")
    public ResponseEntity<ApiResponse<Void>> deleteTask(
            @PathVariable("boardId") Long boardId,
            @PathVariable("sectionId") Long sectionId,
            @PathVariable("taskId") Long taskId,
            @AuthenticationPrincipal String userEmail) {

        kanbanTaskService.deleteTask(boardId, sectionId, taskId, userEmail);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 특정 태스크 정보 변경
     * POST /api/kanban/boards/{boardId}/sections/{sectionId}/tasks/{taskId}/update
     */
    @PostMapping("/{boardId}/sections/{sectionId}/tasks/{taskId}/update")
    public ResponseEntity<ApiResponse<Void>> updateTask(
            @PathVariable("boardId") Long boardId,
            @PathVariable("sectionId") Long sectionId,
            @PathVariable("taskId") Long taskId,
            @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal String userEmail) {

        kanbanTaskService.updateSectionName(boardId, sectionId, taskId, userEmail, request.getTitle(), request.getContent());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
