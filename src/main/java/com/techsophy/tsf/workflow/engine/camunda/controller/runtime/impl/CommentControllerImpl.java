package com.techsophy.tsf.workflow.engine.camunda.controller.runtime.impl;

import com.techsophy.tsf.workflow.engine.camunda.controller.runtime.CommentController;
import com.techsophy.tsf.workflow.engine.camunda.dto.CommentDTO;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import com.techsophy.tsf.workflow.engine.camunda.service.CommentService;
import lombok.AllArgsConstructor;
import org.camunda.bpm.engine.task.Comment;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.COMMENT_CREATED_SUCCESS;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.FETCH_COMMENT_SUCCESS;

/**
 * Rest controller for comments in process or case
 */
@RestController
@AllArgsConstructor
public class CommentControllerImpl implements CommentController
{
    private final CommentService commentService;

    /**
     * create comment for process
     * @param comment
     * @return ApiResponse
     */
    @Override
    public ApiResponse<Comment> createComment(CommentDTO comment)
    {
        return new ApiResponse<>(this.commentService.createComment(comment.getTaskId(), comment.getProcessInstanceId(), comment.getComment()), true, COMMENT_CREATED_SUCCESS);
    }

    /**
     * get comment for process or case
     * @param processInstanceId
     * @param caseInstanceId
     * @param businessKey
     * @return ApiResponse
     */
    @Override
    public ApiResponse<List<Comment>> getComments(String processInstanceId,String caseInstanceId,String businessKey)
    {
        return new ApiResponse<>(this.commentService.getComments(processInstanceId,caseInstanceId,businessKey), true, FETCH_COMMENT_SUCCESS);
    }
}
