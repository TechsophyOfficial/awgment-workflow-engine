package com.techsophy.tsf.workflow.engine.camunda.keycloak.sso;

import com.techsophy.multitenancy.mongo.config.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Keycloak Logout Handler.
 */
@RefreshScope
@Service
public class KeycloakLogoutHandler implements LogoutSuccessHandler
{
	/**
	 * This class' logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(KeycloakLogoutHandler.class);

	/**
	 * Redirect strategy.
	 */
	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	/**
	 * Keycloak's logout URI.
	 */
	private String oauth2UserLogoutUri;


	@Value("${keycloak.realm.name:techsophy-platform}")
	private String defaultRealm;

	/**
	 * Default constructor.
	 *
	 * @param oauth2UserAuthorizationUri configured keycloak authorization URI
	 */
	public KeycloakLogoutHandler(@Value("${spring.security.oauth2.client.provider.keycloak.authorization-uri:}") String oauth2UserAuthorizationUri)
	{
		if (StringUtils.hasLength(oauth2UserAuthorizationUri))
		{
			// in order to get the valid logout uri: simply replace "/auth" at the end of the user authorization uri with "/logout"
			this.oauth2UserLogoutUri = oauth2UserAuthorizationUri.replace("openid-connect/auth", "openid-connect/logout");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException
	{
		if (StringUtils.hasLength(oauth2UserLogoutUri))
		{
			// Calculate redirect URI for Keycloak, something like http://<host:port>/camunda
			String requestUrl = request.getRequestURL().toString();
			String redirectUri = requestUrl.substring(0, requestUrl.indexOf("/app"));
			// Complete logout URL
			String tenant = TenantContext.getTenantId();
			String logoutUrl = oauth2UserLogoutUri + "?redirect_uri=" + redirectUri;

			if(tenant!=null){
				logoutUrl = logoutUrl.replace("realms/" + defaultRealm,"realms/"+tenant) + "?redirect_uri=" + redirectUri;

			}
			// Do logout by redirecting to Keycloak logout
			LOG.debug("Redirecting to logout URL {}", logoutUrl);
			redirectStrategy.sendRedirect(request, response, logoutUrl);
		}
	}
}
