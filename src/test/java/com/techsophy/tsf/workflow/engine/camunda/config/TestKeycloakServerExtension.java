package com.techsophy.tsf.workflow.engine.camunda.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.keycloak.representations.AccessTokenResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class TestKeycloakServerExtension implements AfterAllCallback, BeforeAllCallback
{
    private final WireMockServer keycloak;
    public static final ObjectMapper MAPPER = new ObjectMapper();

    public TestKeycloakServerExtension()
    {
        this.keycloak = new WireMockServer(8180);
    }

    @Override
    public void beforeAll(ExtensionContext context)
    {
        this.keycloak.start();
        this.setupKeycloak();
    }


    public static ResponseDefinitionBuilder json(Object apiResponse)
    {
        return aResponse().withJsonBody(MAPPER.convertValue(apiResponse, JsonNode.class));
    }


    @Override
    public void afterAll(ExtensionContext context)
    {
        this.keycloak.resetAll();
        this.keycloak.stop();
    }

    private void setupKeycloak()
    {
        AccessTokenResponse response = new AccessTokenResponse();

        response.setToken("test-token");
        response.setTokenType("test");
        response.setRefreshToken("test-refresh-token");
        response.setExpiresIn(TimeUnit.DAYS.toMillis(1));

        this.keycloak.stubFor(post("/auth/realms/abc/protocol/openid-connect/token")
                .willReturn(json(response)));

        Map<String, String> group1 = Map.of("id", "group1", "name", "Group 1");
        Map<String, String> group2 = Map.of("id", "group2", "name", "Group 2");

        // user2 belongs to group2 which does not have any accesses
        this.keycloak.stubFor(get(urlPathEqualTo("/auth/admin/realms/abc/users/user2@test.com/groups"))
                .willReturn(json(List.of())));

        // user1 belongs to group1 and group2. group1 has required accesses.
        this.keycloak.stubFor(get(urlPathEqualTo("/auth/admin/realms/abc/users/user1@test.com/groups"))
                .willReturn(json(List.of(group1, group2))));
    }
}
