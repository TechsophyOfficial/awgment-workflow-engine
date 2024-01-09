package com.techsophy.tsf.workflow.engine.camunda.controller.runtime;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.BASE_URL;


@RequestMapping(BASE_URL+"/wrapper")
public interface WrapperController
{
    @RequestMapping(value = "/{appName}",method = {RequestMethod.GET, RequestMethod.POST}, consumes = {MediaType.ALL_VALUE})
    ResponseEntity<Resource> handle(@RequestParam(required = false) MultiValueMap<String, String> params,
                                    RequestEntity<?> requestEntity, @PathVariable("appName") String appName) throws URISyntaxException, IOException;

    @RequestMapping(value = "/{appName}",
            method = {RequestMethod.GET, RequestMethod.POST}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseEntity<Resource> handle(@RequestParam(required = false) MultiValueMap<String, String> params,
                             MultipartHttpServletRequest servletRequest,@PathVariable("appName") String appName) throws IOException, URISyntaxException;

}
