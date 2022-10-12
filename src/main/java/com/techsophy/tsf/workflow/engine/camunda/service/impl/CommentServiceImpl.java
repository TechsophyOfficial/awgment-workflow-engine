package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.techsophy.tsf.workflow.engine.camunda.service.CommentService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricCaseActivityInstance;
import org.camunda.bpm.engine.history.HistoricCaseInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.task.Comment;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service layer for comments
 */
@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService
{
    private final TaskService taskService;
    private final HistoryService historyService;

    /**
     * create comment based on taskid,process instance id and comment
     * @param taskId
     * @param processInstanceId
     * @param comment
     * @return Comment
     */
    @Override
    public Comment createComment(String taskId, String processInstanceId, String comment)
    {
        return this.taskService.createComment(taskId, processInstanceId, comment);
    }

    /**
     * gert comments based on prodess instnace or case instnace or business key
     * @param processInstanceId
     * @param caseInstanceId
     * @param businessKey
     * @return List
     */
    @Override
    public List<Comment> getComments(String processInstanceId,String caseInstanceId,String businessKey)
    {

        List<String> taskList=new ArrayList<>();
        //get tasklist based on process instance id
        if(!StringUtils.isBlank(processInstanceId))
        {
            taskList= historyService.createHistoricActivityInstanceQuery().processInstanceId(processInstanceId).list().stream().map(HistoricActivityInstance::getTaskId).filter(Objects::nonNull).collect(Collectors.toList());
        }
        //get tasklist based on the case instance id
        else if(!StringUtils.isBlank(caseInstanceId))
        {
            taskList=historyService.createHistoricCaseActivityInstanceQuery().caseInstanceId(caseInstanceId).list()
                    .stream().map(HistoricCaseActivityInstance::getTaskId).filter(Objects::nonNull).collect(Collectors.toList());
        }
        //get task list based on business key
        else if(!StringUtils.isBlank(businessKey))
        {
            List<String> processInstanceTaskList=new ArrayList<>();
            List<String> caseInstanceTaskList=new ArrayList<>();
            //get process instance based on business key
            List<String> processInstId=historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(businessKey).list().stream()
                    .map(HistoricProcessInstance::getId).filter(Objects::nonNull)
                    .distinct().collect(Collectors.toList());
            //get tasklist for all the process instance based on businesskey
            for(String procInstId:processInstId)
            {
                List<String> currentProcInstTaskList= historyService.createHistoricActivityInstanceQuery().processInstanceId(procInstId).list()
                        .stream().map(HistoricActivityInstance::getTaskId).filter(Objects::nonNull).collect(Collectors.toList());
                processInstanceTaskList.addAll(currentProcInstTaskList);
            }
            taskList.addAll(processInstanceTaskList);
            //get case instance based on business key
            List<String> caseInstId=historyService.createHistoricCaseInstanceQuery().caseInstanceBusinessKey(businessKey).list().stream()
                    .map(HistoricCaseInstance::getId).filter(Objects::nonNull)
                    .distinct().collect(Collectors.toList());
            //get tasklist for all the case instance based on businesskey
            for(String csInstId:caseInstId)
            {
                List<String> currentCaseInstTaskList= historyService.createHistoricCaseActivityInstanceQuery().caseInstanceId(csInstId).list()
                        .stream().map(HistoricCaseActivityInstance::getTaskId).filter(Objects::nonNull).collect(Collectors.toList());
                caseInstanceTaskList.addAll(currentCaseInstTaskList);
            }
            taskList.addAll(caseInstanceTaskList);
        }
//    get all commnets for the tasks present in the task list
      List<Comment> finalComments=new ArrayList<>();
        for (String task:taskList)
        {
            List<Comment> comments;
            comments=this.taskService.getTaskComments(task);
            finalComments.addAll(comments);
        }
        finalComments.sort(Comparator.comparing(Comment::getTime));
        return finalComments;
    }
}
