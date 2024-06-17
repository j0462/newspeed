package com.example.newspeed.service;

import com.example.newspeed.dto.CommentGetResponse;
import com.example.newspeed.dto.CommentRequest;
import com.example.newspeed.entity.Comment;
import com.example.newspeed.entity.Content;
import com.example.newspeed.entity.User;
import com.example.newspeed.repository.CommentRepository;
import com.example.newspeed.repository.ContentRepository;
import com.example.newspeed.repository.UserRepository;
import com.example.newspeed.security.UserDetailsImpl;
import com.example.newspeed.service.CommentService;
import com.example.newspeed.service.ContentService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // MockitoExtension을 사용하여 Mockito와 함께 사용한다는 것을 선언
public class CommentServiceTest {
    @Mock
    CommentRepository commentRepository;

    @Mock
    ContentService contentService;

    @InjectMocks
    CommentService commentService;

    User user;
    Content content;
    Comment comment;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        user.setPassword("password");

        content = new Content();
        content.setId(1L);
        content.setContent("Test Content");

        comment = new Comment(user, "This is a test comment", content);
        comment.setId(1L);
    }

    @Test
    @DisplayName("댓글 생성 테스트")
    void testCreateComment() {
        // given
        CommentRequest requestDto = new CommentRequest();
        requestDto.setComment("This is a test comment");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        given(contentService.getContentById2(content.getId())).willReturn(content);

        // when
        Long commentId = commentService.create(content.getId(), requestDto, userDetails);

        // then
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());
        Comment savedComment = commentCaptor.getValue();

        assertEquals("This is a test comment", savedComment.getComment());
        assertEquals(user, savedComment.getUser());
        assertEquals(content, savedComment.getNews());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void testUpdateComment() {
        // given
        CommentRequest requestDto = new CommentRequest();
        requestDto.setComment("Updated comment text");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        // when
        Long updatedCommentId = commentService.update(comment.getId(), requestDto, userDetails);

        // then
        assertNotNull(updatedCommentId);
        assertEquals(comment.getId(), updatedCommentId);
        assertEquals("Updated comment text", comment.getComment());
    }

    @Test
    @DisplayName("댓글 조회 테스트")
    void testGetComment() {
        // given
        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));

        // when
        CommentGetResponse response = commentService.get(comment.getId());

        // then
        assertNotNull(response);
        assertEquals(comment.getId(), response.getId());
        assertEquals(comment.getUser().getId(), response.getUserId());
        assertEquals(comment.getComment(), response.getComment());
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void testDeleteComment() {
        // given
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        given(commentRepository.findById(comment.getId())).willReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);

        // when
        Long deletedCommentId = commentService.delete(comment.getId(), userDetails);

        // then
        assertNotNull(deletedCommentId);
        assertEquals(comment.getId(), deletedCommentId);

        verify(commentRepository, times(1)).delete(comment);
    }
}
