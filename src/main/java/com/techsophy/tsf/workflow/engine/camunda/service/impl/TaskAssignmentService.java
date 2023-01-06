package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.exception.InvalidAlgorithmException;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.IdentityLinkType;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.LOAD_BALANCE_ALGORITHM;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.ROUND_ROBIN_ALGORITHM;
import static com.techsophy.tsf.workflow.engine.camunda.constants.ErrorMessageConstants.INVALID_ALGORITHM_EXCEPTION;
import static com.techsophy.tsf.workflow.engine.camunda.constants.ErrorMessageConstants.NO_USERS_IN_GROUP;
import static com.techsophy.tsf.workflow.engine.camunda.utils.CommonUtils.isValidString;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Service
@RequiredArgsConstructor
public class TaskAssignmentService
{
    private final TaskService taskService;
    private final IdentityService identityService;
    private final HistoryService historyService;
    private final GlobalMessageSource globalMessageSource;

    public void setAssigneeByAlgorithm(DelegateTask delegateTask, String algorithm)
    {
        if(!isValidString(algorithm))
        {
            throw new InvalidAlgorithmException(globalMessageSource.get(INVALID_ALGORITHM_EXCEPTION, algorithm));
        }
        switch (algorithm)
        {
            case ROUND_ROBIN_ALGORITHM:
                setAssigneeByRoundRobinAlgorithm(delegateTask);
                break;
            case LOAD_BALANCE_ALGORITHM:
                setAssigneeByLoadBalance(delegateTask);
                break;
            default:
                throw new InvalidAlgorithmException(globalMessageSource.get(INVALID_ALGORITHM_EXCEPTION, algorithm));
        }
    }

    public void setAssigneeByLoadBalance(DelegateTask delegateTask)
    {
        Map<String, Long> userTasksCount = new HashMap<>();
        List<User> users = getUsersFromCandidateGroupByTaskId(delegateTask);
        users.stream().forEach(user -> {
            Long count = taskService.createTaskQuery()
                    .taskDefinitionKey(delegateTask.getTaskDefinitionKey())
                    .taskAssignee(user.getId())
                    .count();
            userTasksCount.put(user.getId(), count);
        });
        String user = Collections.min(userTasksCount.entrySet(), comparing(Map.Entry::getValue)).getKey();
        if(isValidString(user))
        {
            delegateTask.setAssignee(user);
        }
    }

    public void setAssigneeByRoundRobinAlgorithm(DelegateTask delegateTask)
    {
        List<User> users = getUsersFromCandidateGroupByTaskId(delegateTask);
        Iterator<?> usersItr = users.listIterator();
        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                .taskDefinitionKey(delegateTask.getTaskDefinitionKey())
                .taskAssigneeLike("%")
                .orderByHistoricActivityInstanceStartTime().desc()
                .listPage(0, 1);
        String lastTaskAssignedUser = historicTaskInstances.get(0).getAssignee();
        boolean shouldAssignNextUser = false;
        String assignee =users.get(0).getId();
        while (usersItr.hasNext())
        {
            String user = ((User) usersItr.next()).getId();
            if(shouldAssignNextUser || lastTaskAssignedUser == null)
            {

                assignee = user;
                break;
            }
            if(user.equals(lastTaskAssignedUser))
            {
                shouldAssignNextUser = true;
            }

        }

        delegateTask.setAssignee(assignee);
    }

    private List<User> getUsersFromCandidateGroupByTaskId(DelegateTask delegateTask)
    {
        Set<IdentityLink> identities = delegateTask.getCandidates();
        List<User> users = identities.stream()
                .filter(identityLink -> identityLink.getType().equals(IdentityLinkType.CANDIDATE) && identityLink.getGroupId() != null)
                .flatMap(identityLink -> identityService.createUserQuery().memberOfGroup(identityLink.getGroupId()).list().stream())
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparing(User::getId))), ArrayList::new));
        if(users.isEmpty())
        {
            throw new IllegalArgumentException(globalMessageSource.get(NO_USERS_IN_GROUP, delegateTask.getId()));
        }
        return users;
    }
}
