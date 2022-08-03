package com.techsophy.tsf.workflow.engine.camunda.controller.runtime.impl;

import com.techsophy.tsf.workflow.engine.camunda.SwaggerApiEnroll;
import com.techsophy.tsf.workflow.engine.camunda.controller.runtime.FormController;
import com.techsophy.tsf.workflow.engine.camunda.dto.FormSchema;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import com.techsophy.tsf.workflow.engine.camunda.service.RuntimeFormService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RestController;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.PROCESS_RETRIEVED;

/**
 * Wrapper controller
 */
@SwaggerApiEnroll
@RestController
@AllArgsConstructor
public class FormControllerImpl implements FormController
{
    private final RuntimeFormService runtimeFormService;

    /**
     * get form by id from form runtime modeler
     * @param formKey
     * @return ApiResponse
     */
    @Override
    @SneakyThrows
    public ApiResponse<FormSchema> getFormByFormKey(String formKey,String task)
    {
        return new ApiResponse<>(this.runtimeFormService.fetchFormById(formKey,task), true, PROCESS_RETRIEVED);
    }
}
