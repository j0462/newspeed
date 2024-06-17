package com.example.newspeed.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class EntitiyTest {

    @Test
    @DisplayName("Comment")
    public void test1() {
        User user = new User();
        user.setId(1L);
        user.setUserId("testUser");

        Content content = new Content();
        content.setId(1L);
        content.setContent("Test content");

        Comment comment = new Comment(user, "Test comment", content);

        Assertions.assertNotNull(comment);
        Assertions.assertEquals(user, comment.getUser());
        Assertions.assertEquals("Test comment", comment.getComment());
        Assertions.assertEquals(content, comment.getNews());
    }

    @Test
    @DisplayName("Like")
    public void test2() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUserId("testUser");

        Content content = new Content();
        content.setId(1L);
        content.setContent("Test content");

        Comment comment = new Comment(user, "Test comment", content);

        Like like = new Like();
        like.setId(1L);

        // When
        comment.addLike(like);

        // Then
        Assertions.assertEquals(1, comment.getLikeList().size());
        Assertions.assertEquals(comment, like.getComment());
    }

    @Test
    @DisplayName("Content")
    public void test3() {
        User user = new User();
        user.setId(1L);
        user.setUserId("testUser");

        LocalDateTime now = LocalDateTime.now();

        Content content = new Content();
        content.setId(1L);
        content.setUser(user);
        content.setContent("Test content");
        content.setCreatedDate(now);

        Assertions.assertNotNull(content);
        Assertions.assertEquals(user, content.getUser());
        Assertions.assertEquals("Test content", content.getContent());
        Assertions.assertEquals(now, content.getCreatedDate());
    }

    @Test
    @DisplayName("User")
    public void test4() {
        User user = new User();
        user.setId(1L);
        user.setUserId("testUser");
        user.setPassword("Test123!");
        user.setEmail("test@example.com");

        Assertions.assertNotNull(user);
        Assertions.assertEquals("testUser", user.getUserId());
        Assertions.assertEquals("Test123!", user.getPassword());
        Assertions.assertEquals("test@example.com", user.getEmail());
    }
}
