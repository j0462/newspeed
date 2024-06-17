package com.example.newspeed.controller;

import com.example.newspeed.config.WebSecurityConfig;
import com.example.newspeed.dto.CommentRequest;
import com.example.newspeed.entity.User;
import com.example.newspeed.mvc.MockSpringSecurityFilter;
import com.example.newspeed.security.UserDetailsImpl;
import com.example.newspeed.service.CommentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.config.http.MatcherType.mvc;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = { CommentController.class },excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfig.class)
})
public class CommentControllerTest{

    private MockMvc mvc;

    private Principal mockprincipal;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    CommentService commentService;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .build();
    }

    private void mockUserSetup(){
        String id = "testUser123";
        String password = "Test12345!";
        String username = "testUser123";
        String email = "test@example.com";
        String intro = "hello";

        User user = new User(id, password, username, email, intro);
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Principal mockprincipal = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    @Test
    void test1() throws Exception {
        this.mockUserSetup();
        String content = "아무말";
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setComment(content);
        String postInfo = objectMapper.writeValueAsString(commentRequest);

        mvc.perform(post("/api/content/{id}/comment").content(postInfo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockprincipal))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
