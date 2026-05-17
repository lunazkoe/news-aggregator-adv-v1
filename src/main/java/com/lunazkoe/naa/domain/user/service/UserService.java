package com.lunazkoe.naa.domain.user.service;

import com.lunazkoe.naa.domain.user.dto.request.UserRegisterRequest;
import com.lunazkoe.naa.domain.user.dto.response.UserDto;
import com.lunazkoe.naa.domain.user.entity.User;
import com.lunazkoe.naa.domain.user.exception.UserErrorCode;
import com.lunazkoe.naa.domain.user.exception.UserException;
import com.lunazkoe.naa.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 회원가입
     */
    public UserDto register(UserRegisterRequest request) {
        // 이메일 중복 체크
        // TODO: 논리 삭제된 경우 @SQLRestriction 조건에 의해서 찾아지지 않음
        if (userRepository.existsByEmail(request.email())) {
            throw new UserException(UserErrorCode.EMAIL_DUPLICATION);
        }

        User newUser = new User(request.email(), request.email(), request.password());

        User savedUser = userRepository.save(newUser);

        return UserDto.from(savedUser);
    }
}
