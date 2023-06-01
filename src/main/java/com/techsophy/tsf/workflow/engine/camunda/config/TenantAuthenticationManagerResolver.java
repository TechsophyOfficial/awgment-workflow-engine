package com.techsophy.tsf.workflow.engine.camunda.config;

import com.techsophy.tsf.workflow.engine.camunda.utils.TokenUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import static com.techsophy.tsf.workflow.engine.camunda.constants.CamundaRuntimeConstants.AUTHORIZATION;

@RefreshScope
@Component
@AllArgsConstructor(onConstructor_ = {@Autowired})
public class TenantAuthenticationManagerResolver  implements AuthenticationManagerResolver<HttpServletRequest>
{
    private final Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();
    @Value("${keycloak.issuer-uri}")
    private  String keycloakIssuerUri;
    private final TokenUtils tokenUtils;

    @Override
    public AuthenticationManager resolve(HttpServletRequest request)
    {
        return this.authenticationManagers.computeIfAbsent(toTenant(request), this::fromTenant);
    }

    public String toTenant(HttpServletRequest request)
    {
        try
        {
            return tokenUtils.getIssuerFromToken(request.getHeader(AUTHORIZATION));
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(e);
        }
    }

    public AuthenticationManager fromTenant(String tenant)
    {
        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(JwtDecoders.fromIssuerLocation(keycloakIssuerUri+tenant));
        return jwtAuthenticationProvider :: authenticate;
    }
}
