package com.example.newspeed.service;

import com.example.newspeed.dto.CommentGetResponse;
import com.example.newspeed.dto.CommentRequest;
import com.example.newspeed.entity.Comment;
import com.example.newspeed.entity.Content;
import com.example.newspeed.entity.User;
import com.example.newspeed.repository.CommentRepository;
import com.example.newspeed.security.UserDetailsImpl;
import com.example.newspeed.service.CommentService;
import com.example.newspeed.service.ContentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // MockitoExtension을 사용하여 Mockito와 함께 사용한다는 것을 선언
public class CommentServiceTest {

    @InjectMocks // Mock을 주입받을 대상 클래스를 선언
    private CommentService commentService;

    @Mock // Mock 객체를 생성
    private CommentRepository commentRepository;

    @Mock
    private ContentService contentService;

    @Test
    public void testCreateComment() {
        // 테스트에 필요한 Mock 데이터 설정
        Long contentId = 1L;
        CommentRequest request = new CommentRequest();
        request.setComment("Test comment");
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        User mockUser = mock(User.class);
        when(userDetails.getUser()).thenReturn(mockUser);

        Content mockContent = mock(Content.class);
        when(contentService.getContentById2(contentId)).thenReturn(mockContent);

        // 댓글 생성에 대한 Mock 동작 설정
        Comment mockComment = new Comment(mockUser, request.getComment(), mockContent);
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        // 테스트 실행
        Long commentId = commentService.create(contentId, request, userDetails);

        // 결과 확인
        assertNotNull(commentId);
        assertEquals(mockComment.getId(), commentId);
    }
}
