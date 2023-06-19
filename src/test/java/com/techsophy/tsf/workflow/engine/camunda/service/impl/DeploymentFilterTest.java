package com.techsophy.tsf.workflow.engine.camunda.service.impl;

import com.techsophy.tsf.workflow.engine.camunda.keycloak.rest.DeploymentFilter;
import com.techsophy.tsf.workflow.engine.camunda.keycloak.sso.OAuth2AndJwtAwareRequestFilterTest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class DeploymentFilterTest {
    @InjectMocks
    DeploymentFilter deploymentFilter;

    @Mock
    ServletRequest servletRequest;

    @Mock
    ServletResponse servletResponse;

    @Mock
    ServletInputStream servletInputStream;

    DiskFileItemFactory diskFileItemFactoryMock = new DiskFileItemFactory(1024 * 1024, null);

    @Mock
    FileItem fileItem;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;
    @Mock
    ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactoryMock);
    private final String userId = "user@test.com";

    @Test
    void doFilterTest() throws Exception {

        try (MockedStatic<ServletFileUpload> servletFileUploadmocked = Mockito.mockStatic(ServletFileUpload.class);
             MockedStatic<IOUtils> ioUtils = Mockito.mockStatic(IOUtils.class)) {

            ioUtils.when(()->IOUtils.copy((InputStream) any(), (OutputStream) any())).thenReturn(1);
            servletFileUploadmocked.when(() -> ServletFileUpload.isMultipartContent(httpServletRequest)).thenReturn(true);
            Jwt jwt = OAuth2AndJwtAwareRequestFilterTest.getJwt(Map.of("preferred_username", this.userId,"iss","http://techsophy-platform","groups",List.of("awgment-admin")));
            SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
//            when(httpServletRequest.getAttribute("tenant-id")).thenReturn("techsophy-platform");
           when(httpServletRequest.getContentType()).thenReturn(null);
//            when(fileItem.getString()).thenReturn("techsophy-platform");
            List<FileItem> fileItems = new ArrayList<>();
            fileItems.add(fileItem);
            Map<String, List<FileItem>> fileItemMap = new HashMap<>();
            fileItemMap.put("tenant-id", fileItems);
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory(1024 * 1024, null));
//            when(upload.parseParameterMap(httpServletRequest)).thenReturn(fileItemMap);
            when(httpServletRequest.getRequestURI()).thenReturn("/engine-rest/deployment");
//            PowerMockito.whenNew(DiskFileItemFactory.class).withArguments(Mockito.anyInt(), Mockito.any(File.class)).thenReturn(diskFileItemFactoryMock);
//            PowerMockito.whenNew(ServletFileUpload.class).withArguments(diskFileItemFactoryMock).thenReturn(servletFileUpload);
//            when(servletFileUpload.parseParameterMap(any(HttpServletRequest.class))).thenReturn(fileItemMap);
            Assertions.assertThrows(ServletException.class,()->deploymentFilter.doFilter(httpServletRequest, httpServletResponse, filterChain));
//            verify(filterChain).doFilter(httpServletRequest, httpServletResponse);
        }
    }
    
}
