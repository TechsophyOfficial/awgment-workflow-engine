package com.techsophy.tsf.workflow.engine.camunda.controller.runtime;

import com.techsophy.tsf.workflow.engine.camunda.dto.CommentDTO;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import org.camunda.bpm.engine.task.Comment;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.*;

/**
 * comment controller
 */
@RequestMapping(BASE_URL)
public interface CommentController
{
    /**
     * the following method is used to create comment
     * @param comment
     * @return
     */
    @PostMapping(COMMENT_CREATE)
    ApiResponse<Comment> createComment(@RequestBody CommentDTO comment);

    /**
     * the following method is used to get comments based on the case instance or process instance or business key
     * @param processInstanceId
     * @param caseInstanceId
     * @param businessKey
     * @return
     */
    @GetMapping(COMMENT)
    ApiResponse<List<Comment>> getComments(@RequestParam(required = false) String processInstanceId,@RequestParam(required = false) String caseInstanceId,@RequestParam(required = false) String businessKey);
}
