package com.lunazkoe.naa.domain.user.service;

import com.lunazkoe.naa.domain.user.dto.request.UserLoginRequest;
import com.lunazkoe.naa.domain.user.dto.request.UserRegisterRequest;
import com.lunazkoe.naa.domain.user.dto.request.UserUpdateRequest;
import com.lunazkoe.naa.domain.user.dto.response.UserDto;
import com.lunazkoe.naa.domain.user.entity.User;
import com.lunazkoe.naa.domain.user.exception.UserErrorCode;
import com.lunazkoe.naa.domain.user.exception.UserException;
import com.lunazkoe.naa.domain.user.repository.UserRepository;
import java.util.UUID;
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
        // TODO: 논리 삭제된 유저를 어떻게 해야할까?
        if (userRepository.existsByEmail(request.email())) {
            throw new UserException(UserErrorCode.EMAIL_DUPLICATION);
        }

        // Bcrypt로 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        // 유저 생성
        User newUser = new User(request.email(), request.nickname(), encodedPassword);
        User savedUser = userRepository.save(newUser);

        log.info("유저 회원가입 성공. UserId: {}", savedUser.getId());
        return UserDto.from(savedUser);
    }

    /**
     * 로그인
     */
    @Transactional(readOnly = true)
    public UserDto login(UserLoginRequest request) {
        // 이메일 검증
        User foundUser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserException(UserErrorCode.EMAIL_OR_PASSWORD_INVALID));

        // 비밀번호 검증
        boolean isMatch = passwordEncoder.matches(request.password(), foundUser.getPassword());
        if (!isMatch) {
            throw new UserException(UserErrorCode.EMAIL_OR_PASSWORD_INVALID);
        }

        log.info("유저 로그인 성공. UserId: {}", foundUser.getId());
        return UserDto.from(foundUser);
    }

    /**
     * 사용자 논리 삭제
     */
    @Transactional
    public void softDelete(UUID userId) {

        User foundUser = getFoundUserById(userId);

        foundUser.softDelete(); // 더티 체크

        // TODO: 논리 삭제 시 연관관계들에서 일어날 일들 처리

        log.info("유저 논리 삭제 성공. UserId: {}", userId);
    }


    /**
     * 사용자 정보 수정
     */
    @Transactional
    public UserDto updateNickname(UUID userId, UserUpdateRequest request) {
        User foundUser = getFoundUserById(userId);

        foundUser.updateNickname(request.nickname());

        log.info("유저 닉네임 변경 성공. UserId: {}", foundUser.getId());
        return UserDto.from(foundUser);
    }

    /**
     * 사용자 물리 삭제
     * TODO: 나중에 30일 된 유저 일괄 처리 "벌크 작업"
     * - 현재는 이대로 유지, 바로 완전 삭제를 한다는 의미로만 남겨두기
     * - 문제: 논리 삭제된 사용자는 조회가 되지 않음. 물리 삭제를 할 수 없음
     *      - 임시 해결: 사용자는 논리 / 물리 삭제 중 딱 하나만 고를 수 있음
     *      - 즉, 논리 삭제 후 물리 삭제를 명시적으로 할 수 없음
     */
    @Transactional
    public void hardDelete(UUID userId) {

        User foundUser = getFoundUserById(userId);

        userRepository.delete(foundUser);
        // - deleteById를 하지 않는 이유는 굳이 select를 한 번 더 할 필요가 없음

        // TODO: 물리 삭제 시 연관관계들에서 일어날 일들 처리

        log.info("유저 물리 삭제 완료. UserId: {}", foundUser.getId());
    }

    private User getFoundUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }
}
