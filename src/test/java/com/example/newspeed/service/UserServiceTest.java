package com.example.newspeed.service;

import com.example.newspeed.dto.LoginRequestDto;
import com.example.newspeed.dto.SignUpRequestDto;
import com.example.newspeed.entity.User;
import com.example.newspeed.repository.UserRepository;
import com.example.newspeed.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private UserService userService;

    private SignUpRequestDto signUpRequestDto;
    private LoginRequestDto loginRequestDto;

    @BeforeEach
    void setUp() {
        signUpRequestDto = new SignUpRequestDto();
        signUpRequestDto.setUserId("testuser1234");
        signUpRequestDto.setPassword("password123");
        signUpRequestDto.setUsername("TestUser");
        signUpRequestDto.setEmail("testuser@example.com");
        signUpRequestDto.setIntro("Hello, I'm test user");

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUserId("testuser1234");
        loginRequestDto.setPassword("password123");
    }

    @Test
    @DisplayName("회원 가입 성공 테스트")
    void testSignUpSuccess() {
        // given
        given(userRepository.findByUserId(anyString())).willReturn(Optional.empty());
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");

        // when
        assertDoesNotThrow(() -> userService.singUp(signUpRequestDto));

        // then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원 가입 실패 - 이미 존재하는 ID")
    void testSignUpFailureDuplicateUserId() {
        // given
        given(userRepository.findByUserId(anyString())).willReturn(Optional.of(new User()));

        // when, then
        assertThrows(IllegalArgumentException.class, () -> userService.singUp(signUpRequestDto));
    }

    @Test
    @DisplayName("회원 가입 실패 - 이미 존재하는 Email")
    void testSignUpFailureDuplicateEmail() {
        // given
        given(userRepository.findByUserId(anyString())).willReturn(Optional.empty());
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(new User()));

        // when, then
        assertThrows(IllegalArgumentException.class, () -> userService.singUp(signUpRequestDto));
    }

    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    void testWithdrawalSuccess() throws ServletException, IOException {
        // given
        User user = new User("testuser", "encodedPassword", "Test User", "testuser@example.com", "Intro");
        given(userRepository.findByUserId(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);

        // when
        assertDoesNotThrow(() -> userService.withdrawal(loginRequestDto, httpServletResponse));

        // then
        verify(userRepository, times(1)).save(any(User.class));
        verify(httpServletResponse, times(1)).setHeader(anyString(), anyString());
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 비밀번호 불일치")
    void testWithdrawalFailurePasswordMismatch() {
        // given
        given(userRepository.findByUserId(anyString())).willReturn(Optional.of(new User()));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> userService.withdrawal(loginRequestDto, httpServletResponse));
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 사용자가 존재하지 않음")
    void testWithdrawalFailureUserNotFound() {
        // given
        given(userRepository.findByUserId(anyString())).willReturn(Optional.empty());

        // when, then
        assertThrows(UsernameNotFoundException.class, () -> userService.withdrawal(loginRequestDto, httpServletResponse));
    }

    @Test
    @DisplayName("리프레시 토큰 업데이트 성공 테스트")
    void testUpdateRefreshTokenSuccess() {
        // given
        given(userRepository.findByUserId(anyString())).willReturn(Optional.of(new User()));

        // when
        assertDoesNotThrow(() -> userService.updateRefreshToken("testuser", "newRefreshToken"));

        // then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("리프레시 토큰 업데이트 실패 - 존재하지 않는 사용자")
    void testUpdateRefreshTokenFailureUserNotFound() {
        // given
        given(userRepository.findByUserId(anyString())).willReturn(Optional.empty());

        // when, then
        assertThrows(UsernameNotFoundException.class, () -> userService.updateRefreshToken("nonexistentuser", "newRefreshToken"));
    }
}
