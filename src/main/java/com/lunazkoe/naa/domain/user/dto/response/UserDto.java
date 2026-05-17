package com.lunazkoe.naa.domain.user.dto.response;

import com.lunazkoe.naa.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserDto(
        @Schema(description = "사용자 ID")
        UUID id,

        @Schema(description = "이메일")
        String email,

        @Schema(description = "닉네임")
        String nickname,

        @Schema(description = "가입한 날짜")
        LocalDateTime createdAt
) {

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt()
        );
    }
}
