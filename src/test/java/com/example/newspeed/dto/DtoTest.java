package com.example.newspeed.dto;

import com.example.newspeed.dto.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validator;
import com.example.newspeed.entity.Content;
import com.example.newspeed.entity.User;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class DtoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    @DisplayName("CommentGetResponse")
    public void test1() {
        Long id = 1L;
        Long userId = 100L;
        String comment = "Test comment";

        CommentGetResponse response = new CommentGetResponse(id, userId, comment);

        Assertions.assertEquals(id, response.getId());
        Assertions.assertEquals(userId, response.getUserId());
        Assertions.assertEquals(comment, response.getComment());
    }

    @Test
    @DisplayName("CommentGetRequest")
    public void test2() {
        String comment = "Test comment";

        CommentRequest request = new CommentRequest();
        request.setComment(comment);

        Assertions.assertEquals(comment, request.getComment());
    }

    @Test
    @DisplayName("ContentDto")
    public void test3() {
        Long id = 1L;
        User user = new User("testUser", "password", "name", "email", "intro");
        String content = "Test content";
        LocalDateTime createdDate = LocalDateTime.now();
        LocalDateTime updatedDate = LocalDateTime.now();
        Integer likes = 10;

        Content contentEntity = new Content();
        contentEntity.setId(id);
        contentEntity.setUser(user);
        contentEntity.setContent(content);
        contentEntity.setCreatedDate(createdDate);
        contentEntity.setUpdatedDate(updatedDate);
        contentEntity.setLikes(likes);

        ContentDto contentDto = new ContentDto(contentEntity);

        Assertions.assertEquals(id, contentDto.getId());
        Assertions.assertEquals(user, contentDto.getUser());
        Assertions.assertEquals(content, contentDto.getContent());
        Assertions.assertEquals(createdDate, contentDto.getCreatedDate());
        Assertions.assertEquals(updatedDate, contentDto.getUpdatedDate());
        Assertions.assertEquals(likes, contentDto.getLikes());
    }



    @Test
    @DisplayName("ContentRequestDto")
    public void test4() {
        String content = "Test content";

        ContentRequestDto requestDto = new ContentRequestDto();
        requestDto.setContent(content);

        Assertions.assertEquals(content, requestDto.getContent());
    }

    @Test
    @DisplayName("LoginRequestDto")
    public void test5() {
        String userId = "testUser";
        String password = "Test123!";

        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setUserId(userId);
        requestDto.setPassword(password);

        Assertions.assertEquals(userId, requestDto.getUserId());
        Assertions.assertEquals(password, requestDto.getPassword());
    }

    @Test
    @DisplayName("ProfileRequestDto")
    public void test6() {
        long id = 1L;
        String userId = "testUser";
        String password = "Test123!";
        String name = "Test User";
        String email = "test@example.com";
        String intro = "Hello, I'm test user!";
        String newPassword = "NewTest456!";

        ProfileRequestDto requestDto = new ProfileRequestDto();
        requestDto.setId(id);
        requestDto.setUserId(userId);
        requestDto.setPassword(password);
        requestDto.setName(name);
        requestDto.setEmail(email);
        requestDto.setIntro(intro);
        requestDto.setNewPassword(newPassword);

        Assertions.assertEquals(id, requestDto.getId());
        Assertions.assertEquals(userId, requestDto.getUserId());
        Assertions.assertEquals(password, requestDto.getPassword());
        Assertions.assertEquals(name, requestDto.getName());
        Assertions.assertEquals(email, requestDto.getEmail());
        Assertions.assertEquals(intro, requestDto.getIntro());
        Assertions.assertEquals(newPassword, requestDto.getNewPassword());
    }

    @Test
    @DisplayName(" User 생성자를 이용한 ProfileResponseDto")
    public void test7() {
        User user = new User("testUser", "password", "Test User", "test@example.com", "Hello, I'm test user!");

        ProfileResponseDto responseDto = new ProfileResponseDto(user);


        Assertions.assertEquals(user.getUserId(), responseDto.getUserId());
        Assertions.assertEquals(user.getUserName(), responseDto.getName());
        Assertions.assertEquals(user.getEmail(), responseDto.getEmail());
        Assertions.assertEquals(user.getIntro(), responseDto.getIntro());
    }

    @Test
    @DisplayName("필드를 이용한 ProfileResponseDto")
    public void test8() {
        String userId = "testUser";
        String name = "Test User";
        String email = "test@example.com";
        String intro = "Hello, I'm test user!";

        ProfileResponseDto responseDto = new ProfileResponseDto(userId, name, email, intro);

        Assertions.assertEquals(userId, responseDto.getUserId());
        Assertions.assertEquals(name, responseDto.getName());
        Assertions.assertEquals(email, responseDto.getEmail());
        Assertions.assertEquals(intro, responseDto.getIntro());
    }

    @Test
    @DisplayName("toDto 메서드를 이용한 ProfileResponseDto")
    public void test9() {
        User user = new User("testUser", "password", "Test User", "test@example.com", "Hello, I'm test user!");

        ProfileResponseDto responseDto = ProfileResponseDto.toDto(user);

        Assertions.assertEquals(user.getUserId(), responseDto.getUserId());
        Assertions.assertEquals(user.getUserName(), responseDto.getName());
        Assertions.assertEquals(user.getEmail(), responseDto.getEmail());
        Assertions.assertEquals(user.getIntro(), responseDto.getIntro());
    }


    @Test
    @DisplayName("유효한 SignUpRequestDto")
    public void test10() {
        SignUpRequestDto requestDto = new SignUpRequestDto();
        requestDto.setUserId("testUser123");
        requestDto.setPassword("Test12345!");
        requestDto.setEmail("test@example.com");

        Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(requestDto);

        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("유효하지 않은 SignUpRequestDto")
    public void test11() {
        SignUpRequestDto requestDto = new SignUpRequestDto();
        requestDto.setUserId("invalid@user");
        requestDto.setPassword("password");
        requestDto.setEmail("invalid-email");

        Set<ConstraintViolation<SignUpRequestDto>> violations = validator.validate(requestDto);

        assertFalse(violations.isEmpty());
    }
}

