package com.example.spring_boot_jwt_boilerplate.controller;

import com.example.spring_boot_jwt_boilerplate.dto.common.ApiResponse;
import com.example.spring_boot_jwt_boilerplate.dto.kanban.request.KanbanBoardCreateRequest;
import com.example.spring_boot_jwt_boilerplate.dto.kanban.request.KanbanBoardRenameRequest;
import com.example.spring_boot_jwt_boilerplate.dto.kanban.response.BoardDetailResponse;
import com.example.spring_boot_jwt_boilerplate.dto.kanban.response.BoardListResponse;
import com.example.spring_boot_jwt_boilerplate.service.KanbanBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban")
public class KanbanBoardController {

    private final KanbanBoardService kanbanBoardService;

    /**
     * 계정의 칸반 프로젝트 정보를 반환합니다.
     * @param userEmail JwtFilter -> SecurityContextHolder -> Principal
     * @return 리스트로 반환합니다.
     */
    @GetMapping("/boards")
    public ResponseEntity<ApiResponse<List<BoardListResponse>>> getMyBoards(
            @AuthenticationPrincipal String userEmail) {
        List<BoardListResponse> response = kanbanBoardService.getMyBoards(userEmail);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 보드 상세 조회 (섹션 및 태스크 포함)
     * GET /api/kanban/boards/{boardId}
     * @param boardId 보드 ID
     * @param userEmail JwtFilter -> SecurityContextHolder -> Principal
     * @return 리스트로 반환합니다.
     */
    @GetMapping("/boards/{boardId}")
    public ResponseEntity<ApiResponse<BoardDetailResponse>> getBoardDetail(
            @PathVariable Long boardId,
            @AuthenticationPrincipal String userEmail) {

        BoardDetailResponse response = kanbanBoardService.getBoardDetail(boardId, userEmail);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 새 칸반 보드 생성
     * POST /api/kanban
     * @param request   title 제목
     * @param userEmail JwtFilter -> SecurityContextHolder -> Principal
     * @return 보드 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createBoard(
            @RequestBody KanbanBoardCreateRequest request,
            @AuthenticationPrincipal String userEmail) {
        Long createdBoardId = kanbanBoardService.createBoard(request.getTitle(), userEmail);
        return ResponseEntity.ok(new ApiResponse<>(true, "보드가 생성되었습니다.", createdBoardId));
    }

    /**
     * 칸반 보드 삭제
     * DELETE /api/kanban/boards/{boardId}
     * @param boardId 삭제할 보드 ID
     * @param userEmail JwtFilter -> SecurityContextHolder -> Principal
     * @return void (성공 메시지만 전달)
     */
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal String userEmail) {

        kanbanBoardService.deleteBoard(boardId, userEmail);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 칸반 이름 변경
     * POST /api/kanban/boards/{boardId}/rename
     * @param boardId 삭제할 보드 ID
     * @param userEmail JwtFilter -> SecurityContextHolder -> Principal
     * @RequestBody request 바꿀 칸반 보드 이름
     * @return void (성공 메시지만 전달)
     */
    @DeleteMapping("/boards/{boardId}/rename")
    public ResponseEntity<ApiResponse<Void>> renameBoard(
            @PathVariable Long boardId,
            @RequestBody KanbanBoardRenameRequest request,
            @AuthenticationPrincipal String userEmail) {

        kanbanBoardService.updateBoard(boardId, userEmail, request.getTitle());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}