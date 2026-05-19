package com.lunazkoe.naa.domain.comment.repository;

import com.lunazkoe.naa.domain.comment.dto.request.CommentSearchCondition;
import com.lunazkoe.naa.domain.comment.entity.Comment;
import com.lunazkoe.naa.global.dto.CursorPageResponse;

public interface CommentRepositoryCustom {

    CursorPageResponse<Comment> searchComments(CommentSearchCondition condition);
}
