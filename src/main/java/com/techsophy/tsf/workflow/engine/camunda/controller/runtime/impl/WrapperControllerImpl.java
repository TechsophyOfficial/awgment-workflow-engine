package com.techsophy.tsf.workflow.engine.camunda.controller.runtime.impl;

import com.techsophy.tsf.workflow.engine.camunda.controller.runtime.WrapperController;
import com.techsophy.tsf.workflow.engine.camunda.model.ApiResponse;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.RuntimeProcessServiceImpl;
import com.techsophy.tsf.workflow.engine.camunda.service.impl.WrapperServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
@RequiredArgsConstructor
public class WrapperControllerImpl implements WrapperController
{
    private final WrapperServiceImpl wrapperServiceImpl;
    private final RuntimeProcessServiceImpl runtimeProcessService;

    @Override
    public ResponseEntity<Resource> handle(MultiValueMap<String, String> params, RequestEntity<?> request, String appName)
    {
        return wrapperServiceImpl.handleRequest(params, request, appName);
    }

    @Override
    public ResponseEntity<Resource> handle(MultiValueMap<String, String> params, MultipartHttpServletRequest servletRequest,
                                    String appName)
    {
        return wrapperServiceImpl.handleRequest(params, wrapperServiceImpl.getMultipartRequestEntity(servletRequest), appName);
    }

}
