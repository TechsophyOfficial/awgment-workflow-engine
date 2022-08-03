package com.techsophy.tsf.workflow.engine.camunda.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.Resources;
import org.camunda.bpm.engine.rest.AuthorizationRestService;
import org.camunda.bpm.engine.rest.dto.authorization.AuthorizationCreateDto;
import org.camunda.bpm.engine.rest.dto.authorization.AuthorizationDto;
import org.camunda.bpm.engine.rest.impl.AuthorizationRestServiceImpl;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.camunda.bpm.engine.authorization.Resources.*;

public class TestAuthorizationExtension implements BeforeAllCallback, AfterAllCallback
{
    @Override
    public void beforeAll(ExtensionContext context)
    {
        ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);

        ObjectMapper mapper = applicationContext.getBean(ObjectMapper.class);
        ProcessEngine engine = applicationContext.getBean(ProcessEngine.class);

        AuthorizationRestService service = new AuthorizationRestServiceImpl(engine.getName(), mapper);

        List<AuthorizationDto> authorizations = new ArrayList<>();

        authorizations.add(this.addAuthorization(service, "group1", TASK, "READ", "UPDATE", "DELETE"));
        authorizations.add(this.addAuthorization(service, "group1", PROCESS_INSTANCE, "CREATE", "UPDATE_VARIABLE"));
        authorizations.add(this.addAuthorization(service, "group1", PROCESS_DEFINITION, "CREATE_INSTANCE", "READ_INSTANCE"));

        getStore(context).put(AuthorizationRestService.class, service);
        getStore(context).put(TestAuthorizations.class, new TestAuthorizations(authorizations));
    }

    @Override
    public void afterAll(ExtensionContext context)
    {
        AuthorizationRestService service = getStore(context)
                .get(AuthorizationRestService.class, AuthorizationRestService.class);

        TestAuthorizations authorizations = getStore(context)
                .get(TestAuthorizations.class, TestAuthorizations.class);

        authorizations.getAuthorizations()
                .forEach(dto -> service.getAuthorization(dto.getId()).deleteAuthorization());
    }

    private static ExtensionContext.Store getStore(ExtensionContext context)
    {
        return context.getStore(ExtensionContext.Namespace.GLOBAL);
    }

    private AuthorizationDto addAuthorization(AuthorizationRestService authService, String group, Resources resource, String... permissions)
    {
        AuthorizationCreateDto dto = new AuthorizationCreateDto();

        dto.setGroupId(group);
        dto.setPermissions(permissions);
        dto.setType(Authorization.AUTH_TYPE_GRANT);
        dto.setResourceId("*");
        dto.setResourceType(resource.resourceType());

        return authService.createAuthorization(null, dto);
    }

    @Value
    private static class TestAuthorizations
    {
        private final List<AuthorizationDto> authorizations;
    }
}
