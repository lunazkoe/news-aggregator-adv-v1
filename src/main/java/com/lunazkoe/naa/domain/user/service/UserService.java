package com.lunazkoe.naa.domain.user.service;

import com.lunazkoe.naa.domain.user.dto.request.UserRegisterRequest;
import com.lunazkoe.naa.domain.user.dto.response.UserDto;
import com.lunazkoe.naa.domain.user.entity.User;
import com.lunazkoe.naa.domain.user.exception.UserErrorCode;
import com.lunazkoe.naa.domain.user.exception.UserException;
import com.lunazkoe.naa.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     */
    @Transactional
    public UserDto register(UserRegisterRequest request) {
        // 이메일 중복 확인
        // TODO: is_deleted 조건에 따른 설정을 어떻게 할 것인지 추후 논의 => 일단 지금은 새로운 유저 생성
        // - 다만 지금 email에 unique 속성이 없음 => 이거 있어야할지도?
        if (userRepository.existsByEmail(request.email())) {
            throw new UserException(UserErrorCode.EMAIL_DUPLICATION);
        }

        // Bcrypt로 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        // 유저 생성
        User newUser = new User(request.email(), request.nickname(), encodedPassword);
        User savedUser = userRepository.save(newUser);

        return UserDto.from(savedUser);
    }
}
