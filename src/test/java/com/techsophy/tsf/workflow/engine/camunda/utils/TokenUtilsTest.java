package com.techsophy.tsf.workflow.engine.camunda.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techsophy.tsf.workflow.engine.camunda.config.GlobalMessageSource;
import com.techsophy.tsf.workflow.engine.camunda.exception.InvalidInputException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import static com.techsophy.tsf.workflow.engine.camunda.constants.WorkflowEngineConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class TokenUtilsTest
{
    @Mock
    GlobalMessageSource globalMessageSource;
    @Mock
    ObjectMapper objectMapper;
    @Mock
    SecurityContext securityContext;
    @Mock
    SecurityContextHolder securityContextHolder;
    @InjectMocks
    TokenUtils tokenUtils;

    @Test
    void getTokenFromIssuerInvalidInputExceptionTest()
    {
        Assertions.assertThrows(InvalidInputException.class,()->tokenUtils.getIssuerFromToken("Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ2X2NzTUtiOVFsUVpETUg2TXBUdXV1YURtUWstVTQ3bmZjWVZGbFlpLTMwIn0.eyJleHAiOjE2ODU1MjA2MjQsImlhdCI6MTY4NTUxODgyNCwianRpIjoiNzljNDkwYmEtYTU4Zi00MTMxLWJmMzgtZTkwNWEyMDlmODU3IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay10c3BsYXRmb3JtLnRlY2hzb3BoeS5jb20vYXV0aC9yZWFsbXMvdGVjaHNvcGh5LXBsYXRmb3JtIiwiYXVkIjpbImNhbXVuZGEtcmVzdC1hcGkiLCJyZWFsbS1tYW5hZ2VtZW50IiwidGlja2V0aW5nLXN5c3RlbSIsImFjY291bnQiXSwic3ViIjoiY2EwYjAzYjEtMjE3NS00YjI1LWI4NDYtNWYwYzlkNGQ4MWNiIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiY2FtdW5kYS1pZGVudGl0eS1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6ImU5MzU3ZDE2LTBmZjUtNDZlNS1hNWFkLWU5MmE3NjA3MTgwZiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiIsImh0dHA6Ly9sb2NhbGhvc3Q6MzAwMSJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmlldy1yZWFsbSIsInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwibWFuYWdlLWlkZW50aXR5LXByb3ZpZGVycyIsImltcGVyc29uYXRpb24iLCJjcmVhdGUtY2xpZW50IiwibWFuYWdlLXVzZXJzIiwicXVlcnktcmVhbG1zIiwidmlldy1hdXRob3JpemF0aW9uIiwicXVlcnktY2xpZW50cyIsInF1ZXJ5LXVzZXJzIiwibWFuYWdlLWV2ZW50cyIsIm1hbmFnZS1yZWFsbSIsInZpZXctZXZlbnRzIiwidmlldy11c2VycyIsInZpZXctY2xpZW50cyIsIm1hbmFnZS1hdXRob3JpemF0aW9uIiwibWFuYWdlLWNsaWVudHMiLCJxdWVyeS1ncm91cHMiXX19LCJzY29wZSI6ImNhbXVuZGEtcmVzdC1hcGkgcHJvZmlsZSBlbWFpbCBhd2dtZW50IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoidmFpYmhhdiBqYWlzd2FsIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidmFpYmhhdiIsImdpdmVuX25hbWUiOiJ2YWliaGF2IiwiZmFtaWx5X25hbWUiOiJqYWlzd2FsIiwidXNlcklkIjoiMTA5NzQ5MzkwMzU0OTc0MzEwNCIsImVtYWlsIjoidmFpYmhhdi5rQHRlY2hzb3BoeS5jb20ifQ.RQzeV5i8ckllH6OqE_kldXqG6id_goA4LU08j0qj482k90w3VMz5tVfCX_OooIpDEfXhJA2ZPrzieDp4t5iSUq0jKvrqeoKUry7QMH3HgoWIPPD9kE-k6RcIMK63lqwJtMd7vcG-IUOCANq3LhaSj4XY3Inhfg-U_oABOdKAQLk9VDfUwiWUww5vquoNrZ8PqL5EA3J6nxIA72tIIcEt6PUO9Nilchsan-f5UkSjFrtNRHhGZW9mZ0AARnyEaLk_ezM-JEm2fQqWS92tGOxv_GEwM5hp7mv80-Xe-xfrKobfKAQTVFbnR6n1NBqPwsj9KtMfdxAw8LSMBXjLOffBcg"));
    }

    @Test
    void getTokenFromIssuerTest() throws JsonProcessingException
    {
        Map<String,Object> map=new HashMap<>();
        map.put("iss","techsophy-platform");
        when(objectMapper.readValue(anyString(), (TypeReference<Object>) any())).thenReturn(map);
        String tenant = tokenUtils.getIssuerFromToken("Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ2X2NzTUtiOVFsUVpETUg2TXBUdXV1YURtUWstVTQ3bmZjWVZGbFlpLTMwIn0.eyJleHAiOjE2ODU1MjA2MjQsImlhdCI6MTY4NTUxODgyNCwianRpIjoiNzljNDkwYmEtYTU4Zi00MTMxLWJmMzgtZTkwNWEyMDlmODU3IiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay10c3BsYXRmb3JtLnRlY2hzb3BoeS5jb20vYXV0aC9yZWFsbXMvdGVjaHNvcGh5LXBsYXRmb3JtIiwiYXVkIjpbImNhbXVuZGEtcmVzdC1hcGkiLCJyZWFsbS1tYW5hZ2VtZW50IiwidGlja2V0aW5nLXN5c3RlbSIsImFjY291bnQiXSwic3ViIjoiY2EwYjAzYjEtMjE3NS00YjI1LWI4NDYtNWYwYzlkNGQ4MWNiIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiY2FtdW5kYS1pZGVudGl0eS1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6ImU5MzU3ZDE2LTBmZjUtNDZlNS1hNWFkLWU5MmE3NjA3MTgwZiIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiIsImh0dHA6Ly9sb2NhbGhvc3Q6MzAwMSJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmlldy1yZWFsbSIsInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwibWFuYWdlLWlkZW50aXR5LXByb3ZpZGVycyIsImltcGVyc29uYXRpb24iLCJjcmVhdGUtY2xpZW50IiwibWFuYWdlLXVzZXJzIiwicXVlcnktcmVhbG1zIiwidmlldy1hdXRob3JpemF0aW9uIiwicXVlcnktY2xpZW50cyIsInF1ZXJ5LXVzZXJzIiwibWFuYWdlLWV2ZW50cyIsIm1hbmFnZS1yZWFsbSIsInZpZXctZXZlbnRzIiwidmlldy11c2VycyIsInZpZXctY2xpZW50cyIsIm1hbmFnZS1hdXRob3JpemF0aW9uIiwibWFuYWdlLWNsaWVudHMiLCJxdWVyeS1ncm91cHMiXX19LCJzY29wZSI6ImNhbXVuZGEtcmVzdC1hcGkgcHJvZmlsZSBlbWFpbCBhd2dtZW50IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoidmFpYmhhdiBqYWlzd2FsIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidmFpYmhhdiIsImdpdmVuX25hbWUiOiJ2YWliaGF2IiwiZmFtaWx5X25hbWUiOiJqYWlzd2FsIiwidXNlcklkIjoiMTA5NzQ5MzkwMzU0OTc0MzEwNCIsImVtYWlsIjoidmFpYmhhdi5rQHRlY2hzb3BoeS5jb20ifQ.RQzeV5i8ckllH6OqE_kldXqG6id_goA4LU08j0qj482k90w3VMz5tVfCX_OooIpDEfXhJA2ZPrzieDp4t5iSUq0jKvrqeoKUry7QMH3HgoWIPPD9kE-k6RcIMK63lqwJtMd7vcG-IUOCANq3LhaSj4XY3Inhfg-U_oABOdKAQLk9VDfUwiWUww5vquoNrZ8PqL5EA3J6nxIA72tIIcEt6PUO9Nilchsan-f5UkSjFrtNRHhGZW9mZ0AARnyEaLk_ezM-JEm2fQqWS92tGOxv_GEwM5hp7mv80-Xe-xfrKobfKAQTVFbnR6n1NBqPwsj9KtMfdxAw8LSMBXjLOffBcg");
        assertThat(tenant).isEqualTo("techsophy-platform");
    }

    @Test
    void getTokenFromContext()
    {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Jwt jwt = mock(Jwt.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        String token = tokenUtils.getTokenFromContext();
        assertThat(token).isNull();
    }

    @Test
    void getTokenFromContextOAuth2User()
    {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(oAuth2User.getName()).thenReturn("techsophy-platform");
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        Assertions.assertEquals("techsophy-platform",tokenUtils.getTokenFromContext());
    }

    @Test
    void getTokenFromContextSecurityExceptionTest()
    {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(null);
        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> tokenUtils.getTokenFromContext());
    }

    @Test
    void getTokenFromContextException()
    {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        assertThatExceptionOfType(SecurityException.class)
                .isThrownBy(() -> tokenUtils.getLoggedInUserId());
    }

    @Test
    void getLoggedInUserIdOAuth2Test()
    {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        OAuth2User oAuth2User = mock(OAuth2User.class);
        when(authentication.getPrincipal()).thenReturn(oAuth2User);
        when(oAuth2User.getName()).thenReturn("techsophy-platform");
        Assertions.assertEquals("techsophy-platform",tokenUtils.getLoggedInUserId());
    }

    @Test
    void getLoggedInUserIdTest()
    {
        assertThatExceptionOfType(SecurityException.class).isThrownBy(() -> tokenUtils.getLoggedInUserId());
    }

    @Test
    void getTenantTest()
    {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Map<String, Object> claims;
        claims = Map.of(PREFERED_USER_NAME,TENANT);
        Jwt jwt= new Jwt(TOKEN, Instant.now(), Instant.now().plusSeconds(30), Map.of(ALG, NONE), claims);
        when(authentication.getPrincipal()).thenReturn(jwt);
        String token= tokenUtils.getLoggedInUserId();
        assertThat(token).isEqualTo(TENANT);
    }

    @Test
    void getTokenFromContextTest()
    {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
        Assertions.assertThrows(SecurityException.class,()-> tokenUtils.getTokenFromContext());
    }
}
