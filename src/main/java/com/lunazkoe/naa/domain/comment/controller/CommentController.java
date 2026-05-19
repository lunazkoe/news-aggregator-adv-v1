package com.lunazkoe.naa.domain.comment.controller;

import static com.lunazkoe.naa.global.filter.MDCLoggingFilter.HEADER_USER_ID;

import com.lunazkoe.naa.domain.comment.dto.request.CommentRegisterRequest;
import com.lunazkoe.naa.domain.comment.dto.request.CommentUpdateRequest;
import com.lunazkoe.naa.domain.comment.dto.response.CommentDto;
import com.lunazkoe.naa.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 등록", description = "새로운 댓글을 등록합니다.")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto registerComment(@Valid @RequestBody CommentRegisterRequest request) {
        return commentService.createComment(request);
    }

    @Operation(summary = "댓글 논리 삭제", description = "댓글을 논리적으로 삭제합니다.")
    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeleteComment(@PathVariable UUID commentId) {
        commentService.softDelete(commentId);
    }

    @Operation(summary = "댓글 정보 수정", description = "댓글의 내용을 수정합니다.")
    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateCommentContent(@PathVariable UUID commentId,
            @RequestHeader(HEADER_USER_ID) UUID requestUserId,
            @Valid @RequestBody CommentUpdateRequest request) {
        return commentService.updateCommentContent(commentId, requestUserId, request);
    }

    @Operation(summary = "댓글 물리 삭제", description = "댓글을 물리적으로 삭제합니다.")
    @DeleteMapping("/{commentId}/hard")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void hardDeleteComment(@PathVariable UUID commentId) {
        commentService.hardDelete(commentId);
    }
}
