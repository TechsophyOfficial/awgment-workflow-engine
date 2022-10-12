package com.techsophy.tsf.workflow.engine.camunda.controller;

import com.techsophy.tsf.workflow.engine.camunda.controller.runtime.impl.CommentControllerImpl;
import com.techsophy.tsf.workflow.engine.camunda.dto.CommentDTO;
import com.techsophy.tsf.workflow.engine.camunda.exception.GlobalExceptionHandler;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import com.techsophy.tsf.workflow.engine.camunda.service.CommentService;
import org.camunda.bpm.engine.task.Comment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;
import static com.techsophy.tsf.workflow.engine.camunda.constants.FormConstants.BUSINESS_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@ExtendWith(MockitoExtension.class)
public class CommentControllerImplTest {

    private static  final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRead = jwt().authorities(new SimpleGrantedAuthority("READ"));
    private static  final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtSaveOrUpdate = jwt().authorities(new SimpleGrantedAuthority("AWGMENT_FORM_CREATE_OR_UPDATE"));

    @Mock
    CommentService commentService;
    @Mock
    Comment comment;
    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    CommentControllerImpl commentController;

    @BeforeEach
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createCommentTest(){
        ApiResponse<Comment> result = new ApiResponse<>(comment,true,COMMENT_CREATED_SUCCESS);
        CommentDTO commentDTO = new CommentDTO("taskId","processInstanceId","comment");
        Mockito.when(commentService.createComment(any(),any(),any())).thenReturn(comment);
        ApiResponse<Comment> response = commentController.createComment(commentDTO);
        Assertions.assertEquals(result,response);
    }

    @Test
    void getCommentsTest(){
        List<Comment> data = new ArrayList<>();
        data.add(comment);
        ApiResponse<List<Comment>> result = new ApiResponse<>(data,true,FETCH_COMMENT_SUCCESS);
        Mockito.when(commentService.getComments(any(),any(),any())).thenReturn(data);
        ApiResponse<List<Comment>> response = commentController.getComments(PROCESS_INSTANCE_ID,"caseInstanceId",BUSINESS_KEY);
        Assertions.assertEquals(result,response);
    }
}
