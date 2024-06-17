package com.example.newspeed.service;

import com.example.newspeed.entity.Comment;
import com.example.newspeed.entity.Content;
import com.example.newspeed.entity.Like;
import com.example.newspeed.entity.User;
import com.example.newspeed.repository.CommentRepository;
import com.example.newspeed.repository.ContentRepository;
import com.example.newspeed.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private LikeService likeService;

    private User user;
    private Content content;
    private Comment comment;

    @BeforeEach
    void setUp() {
        // Mock data setup
        user = new User();
        user.setId(1L);
        user.setUserName("testuser");

        content = new Content();
        content.setId(1L);
        content.setContent("Test Content");
        content.setUser(user);

        comment = new Comment();
        comment.setId(1L);
        comment.setComment("Test Comment");
        comment.setUser(user);
    }

    @Test
    @DisplayName("게시물 좋아요 테스트")
    void testContentLike() {
        // given
        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));
        when(likeRepository.findByUserAndContent(any(User.class), any(Content.class))).thenReturn(Optional.empty());
        doNothing().when(content).addLike(any(Like.class));
        when(likeRepository.save(any(Like.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ResponseEntity<String> response = likeService.contentLike(content.getId(), user);

        // then
        assertEquals("좋아요 성공.", response.getBody());
        assertEquals(ResponseEntity.ok().build().getStatusCodeValue(), response.getStatusCodeValue());
        verify(contentRepository, times(1)).findById(content.getId());
        verify(likeRepository, times(1)).findByUserAndContent(eq(user), eq(content));
        verify(content, times(1)).addLike(any(Like.class));
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    @DisplayName("게시물 좋아요 중복 방지 테스트")
    void testContentLikeDuplicate() {
        // given
        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));
        when(likeRepository.findByUserAndContent(any(User.class), any(Content.class))).thenReturn(Optional.of(new Like()));

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.contentLike(content.getId(), user));

        // then
        assertEquals("이미 좋아요를 누른 게시글 입니다.", exception.getMessage());
        verify(contentRepository, times(1)).findById(content.getId());
        verify(likeRepository, times(1)).findByUserAndContent(eq(user), eq(content));
        verify(content, never()).addLike(any(Like.class));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    @DisplayName("게시물 좋아요 취소 테스트")
    void testContentUnlike() {
        // given
        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));
        when(likeRepository.findByUserAndContent(any(User.class), any(Content.class))).thenReturn(Optional.of(new Like()));
        doNothing().when(content).removeLike(any(Like.class));
        doNothing().when(likeRepository).delete(any(Like.class));

        // when
        ResponseEntity<String> response = likeService.contentUnlike(content.getId(), user);

        // then
        assertEquals("좋아요 취소 완료.", response.getBody());
        assertEquals(ResponseEntity.ok().build().getStatusCodeValue(), response.getStatusCodeValue());
        verify(contentRepository, times(1)).findById(content.getId());
        verify(likeRepository, times(1)).findByUserAndContent(eq(user), eq(content));
        verify(content, times(1)).removeLike(any(Like.class));
        verify(likeRepository, times(1)).delete(any(Like.class));
    }

    @Test
    @DisplayName("게시물 좋아요 취소 실패 테스트")
    void testContentUnlikeFail() {
        // given
        when(contentRepository.findById(content.getId())).thenReturn(Optional.of(content));
        when(likeRepository.findByUserAndContent(any(User.class), any(Content.class))).thenReturn(Optional.empty());

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.contentUnlike(content.getId(), user));

        // then
        assertEquals("이 게시물에 좋아요를 한 적이 없습니다.", exception.getMessage());
        verify(contentRepository, times(1)).findById(content.getId());
        verify(likeRepository, times(1)).findByUserAndContent(eq(user), eq(content));
        verify(content, never()).removeLike(any(Like.class));
        verify(likeRepository, never()).delete(any(Like.class));
    }

    @Test
    @DisplayName("댓글 좋아요 테스트")
    void testCommentLike() {
        // given
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(likeRepository.findByUserAndComment(any(User.class), any(Comment.class))).thenReturn(Optional.empty());
        doNothing().when(comment).addLike(any(Like.class));
        when(likeRepository.save(any(Like.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ResponseEntity<String> response = likeService.commentLike(comment.getId(), user);

        // then
        assertEquals("좋아요 성공.", response.getBody());
        assertEquals(ResponseEntity.ok().build().getStatusCodeValue(), response.getStatusCodeValue());
        verify(commentRepository, times(1)).findById(comment.getId());
        verify(likeRepository, times(1)).findByUserAndComment(eq(user), eq(comment));
        verify(comment, times(1)).addLike(any(Like.class));
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    @DisplayName("댓글 좋아요 중복 방지 테스트")
    void testCommentLikeDuplicate() {
        // given
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(likeRepository.findByUserAndComment(any(User.class), any(Comment.class))).thenReturn(Optional.of(new Like()));

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.commentLike(comment.getId(), user));

        // then
        assertEquals("이미 좋아요를 누른 댓글 입니다.", exception.getMessage());
        verify(commentRepository, times(1)).findById(comment.getId());
        verify(likeRepository, times(1)).findByUserAndComment(eq(user), eq(comment));
        verify(comment, never()).addLike(any(Like.class));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    @DisplayName("댓글 좋아요 취소 테스트")
    void testCommentUnlike() {
        // given
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(likeRepository.findByUserAndComment(any(User.class), any(Comment.class))).thenReturn(Optional.of(new Like()));
        doNothing().when(comment).removeLike(any(Like.class));
        doNothing().when(likeRepository).delete(any(Like.class));

        // when
        ResponseEntity<String> response = likeService.commentUnlike(comment.getId(), user);

        // then
        assertEquals(" 좋아요 취소 완료.", response.getBody());
        assertEquals(ResponseEntity.ok().build().getStatusCodeValue(), response.getStatusCodeValue());
        verify(commentRepository, times(1)).findById(comment.getId());
        verify(likeRepository, times(1)).findByUserAndComment(eq(user), eq(comment));
        verify(comment, times(1)).removeLike(any(Like.class));
        verify(likeRepository, times(1)).delete(any(Like.class));
    }

    @Test
    @DisplayName("댓글 좋아요 취소 실패 테스트")
    void testCommentUnlikeFail() {
        // given
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        when(likeRepository.findByUserAndComment(any(User.class), any(Comment.class))).thenReturn(Optional.empty());

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.commentUnlike(comment.getId(), user));

        // then
        assertEquals("이 댓글에 좋아요를 한 적이 없습니다.", exception.getMessage());
        verify(commentRepository, times(1)).findById(comment.getId());
        verify(likeRepository, times(1)).findByUserAndComment(eq(user), eq(comment));
        verify(comment, never()).removeLike(any(Like.class));
        verify(likeRepository, never()).delete(any(Like.class));
    }
}