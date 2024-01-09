package com.techsophy.tsf.workflow.engine.camunda.config;

import com.techsophy.tsf.workflow.engine.camunda.utils.TokenUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class TenantAuthenticationManagerResolverTest
{
    @Mock
    JWTRoleConverter jwtRoleConverter;
    @Mock
    TokenUtils tokenUtils;
    @InjectMocks
    TenantAuthenticationManagerResolver tenantAuthenticationManagerResolver;

    @Test
    void toTenantTest()
    {
        Mockito.when(tokenUtils.getIssuerFromToken(anyString())).thenReturn("techsophy-platform");
        MockHttpServletRequest mockHttpServletRequest=new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("Authorization","Bearer ");
        Assertions.assertEquals("techsophy-platform",tenantAuthenticationManagerResolver.toTenant(mockHttpServletRequest));
    }

    @Test
    void toTenantIllegalArgumentExceptionTest()
    {
        Mockito.when(tokenUtils.getIssuerFromToken(anyString())).thenThrow(new IllegalArgumentException());
        MockHttpServletRequest mockHttpServletRequest=new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("Authorization","Bearer ");
        Assertions.assertThrows(IllegalArgumentException.class,()->tenantAuthenticationManagerResolver.toTenant(mockHttpServletRequest));
    }

    @Test
    void resolveTest()
    {
        ReflectionTestUtils.setField(tenantAuthenticationManagerResolver,"keycloakIssuerUri","https://keycloak-tsplatform.techsophy.com/auth/realms/");
        Mockito.when(tokenUtils.getIssuerFromToken(anyString())).thenReturn("techsophy-platform");
        MockHttpServletRequest mockHttpServletRequest=new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("Authorization","Bearer eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ2X2NzTUtiOVFsUVpETUg2TXBUdXV1YURtUWstVTQ3bmZjWVZGbFlpLTMwIn0.eyJleHAiOjE2ODU1MzIzMjksImlhdCI6MTY4NTUzMDUyOSwianRpIjoiYzY5OTU1Y2UtMThiYy00YmJkLWJjNTctOTNiYmRlNjExNTNhIiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay10c3BsYXRmb3JtLnRlY2hzb3BoeS5jb20vYXV0aC9yZWFsbXMvdGVjaHNvcGh5LXBsYXRmb3JtIiwiYXVkIjpbImNhbXVuZGEtcmVzdC1hcGkiLCJyZWFsbS1tYW5hZ2VtZW50IiwidGlja2V0aW5nLXN5c3RlbSIsImFjY291bnQiXSwic3ViIjoiY2EwYjAzYjEtMjE3NS00YjI1LWI4NDYtNWYwYzlkNGQ4MWNiIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiY2FtdW5kYS1pZGVudGl0eS1zZXJ2aWNlIiwic2Vzc2lvbl9zdGF0ZSI6IjAxMGMwODBjLTU3YmYtNDlmNy1iNDJmLWE4ODYxMmUyOWRkYSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiIsImh0dHA6Ly9sb2NhbGhvc3Q6MzAwMSJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InJlYWxtLW1hbmFnZW1lbnQiOnsicm9sZXMiOlsidmlldy1yZWFsbSIsInZpZXctaWRlbnRpdHktcHJvdmlkZXJzIiwibWFuYWdlLWlkZW50aXR5LXByb3ZpZGVycyIsImltcGVyc29uYXRpb24iLCJjcmVhdGUtY2xpZW50IiwibWFuYWdlLXVzZXJzIiwicXVlcnktcmVhbG1zIiwidmlldy1hdXRob3JpemF0aW9uIiwicXVlcnktY2xpZW50cyIsInF1ZXJ5LXVzZXJzIiwibWFuYWdlLWV2ZW50cyIsIm1hbmFnZS1yZWFsbSIsInZpZXctZXZlbnRzIiwidmlldy11c2VycyIsInZpZXctY2xpZW50cyIsIm1hbmFnZS1hdXRob3JpemF0aW9uIiwibWFuYWdlLWNsaWVudHMiLCJxdWVyeS1ncm91cHMiXX19LCJzY29wZSI6ImNhbXVuZGEtcmVzdC1hcGkgcHJvZmlsZSBlbWFpbCBhd2dtZW50IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoidmFpYmhhdiBqYWlzd2FsIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidmFpYmhhdiIsImdpdmVuX25hbWUiOiJ2YWliaGF2IiwiZmFtaWx5X25hbWUiOiJqYWlzd2FsIiwidXNlcklkIjoiMTA5NzQ5MzkwMzU0OTc0MzEwNCIsImVtYWlsIjoidmFpYmhhdi5rQHRlY2hzb3BoeS5jb20ifQ.JAiCTrEcQOEaDOckivLqkffV5lia71B0qUOz71HOJeyhIwJdEUWiiqnAhulFupDqjxQDHOowsUXS5Ygn4yJVmnh4kj6RwVhFFqFqbdZ_dtSXLH8ZbSPbNuuWpPACfAtHCY2Q0-TGvXrU0pXAwnCxVkeaG3kOomrx8mePPGFkupvh26SkO6GWbBMhBRllHntTX_ElIflbpS4Pz4q53Li1Kt8BDQkI3miyepyv-_hBhTVvF_OHF2qJxu6e2iHNykTE0NZUPfopyqmecC9GM9slOdHGadUZ0DBTxm7kwkXxt3KhJeKybr_SP3nEUUs-A9fW5AUFaVe0WfU7dvZh82Dn7w");
        Assertions.assertNotNull(tenantAuthenticationManagerResolver.resolve(mockHttpServletRequest));
    }
}
