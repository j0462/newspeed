package com.example.newspeed.service;

import com.example.newspeed.dto.ProfileRequestDto;
import com.example.newspeed.dto.ProfileResponseDto;
import com.example.newspeed.entity.User;
import com.example.newspeed.repository.UserRepository;
import com.example.newspeed.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ProfileService profileService;

    private User user;
    private UserDetailsImpl userDetails;
    private ProfileRequestDto profileRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUserId("testuser");
        user.setUserName("testuser");
        user.setPassword("password");
        user.setEmail("Test@naver.com");
        user.setIntro("Hello, I'm test user");

        userDetails = new UserDetailsImpl(user);

        profileRequestDto = new ProfileRequestDto();
        profileRequestDto.setEmail("Test@naver.com");
        profileRequestDto.setId(1L);
        profileRequestDto.setUserId("testuser");
        profileRequestDto.setName("testuser");
        profileRequestDto.setIntro("Updated intro");
        profileRequestDto.setPassword("password");
        profileRequestDto.setNewPassword("newPassword");
    }

    @Test
    @DisplayName("프로필 조회 테스트")
    void testGetProfile() {
        // given
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        // when
        ProfileResponseDto result = profileService.getProfile(user.getId());

        // then
        assertEquals(user.getUserId(), result.getUserId());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getIntro(), result.getIntro());
    }

    @Test
    @DisplayName("프로필 업데이트 테스트")
    void testUpdateProfile() {
        // when
        ProfileResponseDto result = profileService.update(userDetails, profileRequestDto);

        // then
        assertNotNull(result);
        assertEquals(profileRequestDto.getName(), result.getName());
        assertEquals(profileRequestDto.getIntro(), result.getIntro());
    }

    @Test
    @DisplayName("비밀번호 업데이트 성공 테스트")
    void testUpdatePasswordSuccess() {
        // when
        assertDoesNotThrow(() -> profileService.updatePassword(userDetails, profileRequestDto));
    }

    @Test
    @DisplayName("비밀번호 업데이트 실패 - 기존 비밀번호 불일치")
    void testUpdatePasswordFailurePasswordMismatch() {
        // given
        profileRequestDto.setPassword("wrongPassword");

        // when, then
        assertThrows(IllegalArgumentException.class, () -> profileService.updatePassword(userDetails, profileRequestDto));
    }

    @Test
    @DisplayName("비밀번호 업데이트 실패 - 새로운 비밀번호가 이전과 같음")
    void testUpdatePasswordFailureSameNewPassword() {
        // given
        profileRequestDto.setNewPassword("password");

        // when, then
        assertThrows(IllegalArgumentException.class, () -> profileService.updatePassword(userDetails, profileRequestDto));
    }
}
