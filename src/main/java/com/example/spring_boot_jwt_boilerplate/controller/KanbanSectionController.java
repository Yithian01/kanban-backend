package com.example.spring_boot_jwt_boilerplate.controller;

import com.example.spring_boot_jwt_boilerplate.dto.common.ApiResponse;
import com.example.spring_boot_jwt_boilerplate.dto.section.CreateSectionRequest;
import com.example.spring_boot_jwt_boilerplate.dto.section.MoveSectionRequest;
import com.example.spring_boot_jwt_boilerplate.dto.section.UpdateSectionRequest;
import com.example.spring_boot_jwt_boilerplate.service.KanbanSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban/boards")
public class KanbanSectionController {

    private final KanbanSectionService kanbanSectionService;

    /**
     * 특정 보드 내에 새로운 섹션 생성
     * POST /api/kanban/boards/{boardId}/sections
     */
    @PostMapping("/{boardId}/sections")
    public ResponseEntity<ApiResponse<Void>> createSection(
            @PathVariable Long boardId,
            @RequestBody CreateSectionRequest request,
            @AuthenticationPrincipal String userEmail) {

        kanbanSectionService.addSection(boardId, request.getName(), userEmail);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 특정 섹션 삭제
     * DELETE /api/kanban/boards/{boardId}/sections/{sectionId}
     */
    @DeleteMapping("/{boardId}/sections/{sectionId}")
    public ResponseEntity<ApiResponse<Void>> deleteSection(
            @PathVariable Long boardId,
            @PathVariable Long sectionId,
            @AuthenticationPrincipal String userEmail) {

        kanbanSectionService.deleteSection(boardId, sectionId, userEmail);

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 특정 섹션 이름 변경
     * POST /api/kanban/boards/{boardId}/sections/{sectionId}
     */
    @PostMapping("/{boardId}/sections/{sectionId}")
    public ResponseEntity<ApiResponse<Void>> updateSection(
            @PathVariable Long boardId,
            @PathVariable Long sectionId,
            @RequestBody UpdateSectionRequest request,
            @AuthenticationPrincipal String userEmail) {

        kanbanSectionService.updateSectionName(boardId, sectionId, userEmail, request.getName());

        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 특정 섹션 위치 변경
     */
    @PostMapping("/{boardId}/sections/{sectionId}/position")
    public ResponseEntity<ApiResponse<Void>> updateSectionPosition(
            @PathVariable("boardId") Long boardId,
            @PathVariable("sectionId") Long sectionId,
            @RequestBody MoveSectionRequest request,
            @AuthenticationPrincipal String userEmail) {

        kanbanSectionService.updateSectionPosition(boardId, sectionId, userEmail, request.getTargetIndex());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}