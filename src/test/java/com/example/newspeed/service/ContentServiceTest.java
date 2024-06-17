package com.example.newspeed.service;

import com.example.newspeed.dto.CommentGetResponse;
import com.example.newspeed.dto.CommentRequest;
import com.example.newspeed.dto.ContentDto;
import com.example.newspeed.entity.Comment;
import com.example.newspeed.entity.Content;
import com.example.newspeed.entity.User;
import com.example.newspeed.repository.CommentRepository;
import com.example.newspeed.repository.ContentRepository;
import com.example.newspeed.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {
    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentService contentService;

    private User user;
    private Content content;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUserName("testuser");
        user.setPassword("password");

        content = new Content();
        content.setId(1L);
        content.setContent("Test Content");
        content.setUser(user);
        content.setContent("Sample content");
        content.setCreatedDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("모든 컨텐츠 조회 테스트")
    void testGetAllContents() {
        // given
        List<Content> contentList = new ArrayList<>();
        contentList.add(content);
        given(contentRepository.findAll()).willReturn(contentList);

        // when
        List<ContentDto> result = contentService.getAllContents();

        // then
        assertEquals(1, result.size());
        ContentDto retrievedContentDto = result.get(0);
        assertEquals(content.getId(), retrievedContentDto.getId());
        assertEquals(content.getContent(), retrievedContentDto.getContent());
        assertEquals(content.getContent(), retrievedContentDto.getContent());
    }

    @Test
    @DisplayName("ID로 컨텐츠 조회 테스트")
    void testGetContentById() {
        // given
        given(contentRepository.findById(content.getId())).willReturn(Optional.of(content));

        // when
        ContentDto result = contentService.getContentById(content.getId());

        // then
        assertEquals(content.getId(), result.getId());
        assertEquals(content.getContent(), result.getContent());
        assertEquals(content.getContent(), result.getContent());
    }

    @Test
    @DisplayName("컨텐츠 생성 테스트")
    void testCreateContent() {
        // given
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String newContentText = "New content to be saved";

        given(contentRepository.save(any(Content.class))).willAnswer(invocation -> {
            Content savedContent = invocation.getArgument(0);
            savedContent.setId(2L); // Set a dummy ID
            return savedContent;
        });

        // when
        ContentDto result = contentService.createContent(userDetails, newContentText);

        // then
        assertEquals(newContentText, result.getContent());
        assertEquals(user.getId(), result.getUser().getId());
    }

    @Test
    @DisplayName("컨텐츠 수정 테스트")
    void testUpdateContent() {
        // given
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        String updatedContentText = "Updated content";

        given(contentRepository.findById(content.getId())).willReturn(Optional.of(content));
        given(contentRepository.save(any(Content.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        ContentDto result = contentService.updateContent(content.getId(), userDetails, updatedContentText);

        // then
        assertEquals(updatedContentText, result.getContent());
        assertEquals(user.getId(), result.getUser().getId());
    }

    @Test
    @DisplayName("컨텐츠 삭제 테스트")
    void testDeleteContent() {
        // given
        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        given(contentRepository.findById(content.getId())).willReturn(Optional.of(content));
        doNothing().when(contentRepository).delete(content);

        // when
        assertDoesNotThrow(() -> contentService.deleteContent(content.getId(), userDetails));

        // then
        verify(contentRepository, times(1)).delete(content);
    }

    @Test
    @DisplayName("페이지네이션을 이용한 컨텐츠 조회 테스트")
    void testGetContentsWithPagination() {
        // given
        int page = 0;
        int size = 10;
        String sortBy = "createdDate";
        PageRequest pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        List<Content> contentList = new ArrayList<>();
        contentList.add(content);
        Page<Content> contentPage = new PageImpl<>(contentList, pageable, contentList.size());

        given(contentRepository.findAll(pageable)).willReturn(contentPage);

        // when
        Page<ContentDto> resultPage = contentService.getContents(page, size, sortBy);

        // then
        assertEquals(contentPage.getTotalElements(), resultPage.getTotalElements());
        assertEquals(contentPage.getTotalPages(), resultPage.getTotalPages());
        assertEquals(1, resultPage.getContent().size());
        ContentDto retrievedContentDto = resultPage.getContent().get(0);
        assertEquals(content.getId(), retrievedContentDto.getId());
        assertEquals(content.getContent(), retrievedContentDto.getContent());
        assertEquals(content.getContent(), retrievedContentDto.getContent());
    }

    @Test
    @DisplayName("컨텐츠 생성일자로 정렬된 컨텐츠 조회 테스트")
    void testGetContentsSortedByCreatedAt() {
        // given
        int page = 0;
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        List<Content> contentList = new ArrayList<>();
        contentList.add(content);
        Page<Content> contentPage = new PageImpl<>(contentList, pageable, contentList.size());

        given(contentRepository.findAll(pageable)).willReturn(contentPage);

        // when
        Page<ContentDto> resultPage = contentService.getContentsSortedByCreatedAt(page, size);

        // then
        assertEquals(contentPage.getTotalElements(), resultPage.getTotalElements());
        assertEquals(contentPage.getTotalPages(), resultPage.getTotalPages());
        assertEquals(1, resultPage.getContent().size());
        ContentDto retrievedContentDto = resultPage.getContent().get(0);
        assertEquals(content.getId(), retrievedContentDto.getId());
        assertEquals(content.getContent(), retrievedContentDto.getContent());
        assertEquals(content.getContent(), retrievedContentDto.getContent());
    }

    @Test
    @DisplayName("특정 기간 내의 컨텐츠 조회 테스트")
    void testSearchContentsByDateRange() {
        // given
        int page = 0;
        int size = 10;
        LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 12, 31, 23, 59);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        List<Content> contentList = new ArrayList<>();
        contentList.add(content);
        Page<Content> contentPage = new PageImpl<>(contentList, pageable, contentList.size());

        given(contentRepository.findByCreatedDateBetween(startDate, endDate, pageable)).willReturn(contentPage);

        // when
        Page<ContentDto> resultPage = contentService.searchContentsByDateRange(startDate, endDate, page, size);

        // then
        assertEquals(contentPage.getTotalElements(), resultPage.getTotalElements());
        assertEquals(contentPage.getTotalPages(), resultPage.getTotalPages());
        assertEquals(1, resultPage.getContent().size());
        ContentDto retrievedContentDto = resultPage.getContent().get(0);
        assertEquals(content.getId(), retrievedContentDto.getId());
        assertEquals(content.getContent(), retrievedContentDto.getContent());
        assertEquals(content.getContent(), retrievedContentDto.getContent());
    }

    @Test
    @DisplayName("좋아요 순으로 정렬된 컨텐츠 조회 테스트")
    void testGetContentsOrderByLikes() {
        // given
        int page = 0;
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size, Sort.by("likes").descending());
        List<Content> contentList = new ArrayList<>();
        contentList.add(content);
        Page<Content> contentPage = new PageImpl<>(contentList, pageable, contentList.size());

        given(contentRepository.findAllByOrderByLikesDesc(pageable)).willReturn(contentPage);

        // when
        Page<ContentDto> resultPage = contentService.getContentsOrderByLikes(page, size);

        // then
        assertEquals(contentPage.getTotalElements(), resultPage.getTotalElements());
        assertEquals(contentPage.getTotalPages(), resultPage.getTotalPages());
        assertEquals(1, resultPage.getContent().size());
        ContentDto retrievedContentDto = resultPage.getContent().get(0);
        assertEquals(content.getId(), retrievedContentDto.getId());
    }
}
