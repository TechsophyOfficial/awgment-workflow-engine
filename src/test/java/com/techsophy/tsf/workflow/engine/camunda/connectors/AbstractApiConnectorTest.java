package com.techsophy.tsf.workflow.engine.camunda.connectors;

import com.techsophy.tsf.workflow.engine.camunda.config.TestSecurityConfig;
import lombok.AllArgsConstructor;
import org.apache.http.Header;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@AllArgsConstructor(onConstructor_ = {@Autowired})
@SpringBootTest(classes = TestSecurityConfig.class)
class AbstractApiConnectorTest
{

    private final TestConnector connector;

//    @Test
//    void shouldDiscoverConnectorTest()
//    {
//
//        Assertions.assertNotNull(this.connector);
//    }

//    @Test
//    public void shouldEnrichRequest() throws IOException
//    {
//        setMockResponse();
//
//        this.connector.execute(this.connector.createRequest().header("Content-Type", "application/json").post());
//
//        HttpPost target = this.connector.getInterceptor().getTarget();
//
//        assertThat(target.getURI().toASCIIString()).isEqualTo("http://localhost:8888/test-service");
//
//        Set<String> headers = Arrays.stream(target.getAllHeaders())
//                .map(header -> header.getName() + ": " + header.getValue())
//                .collect(Collectors.toSet());
//
//        assertThat(headers).isEqualTo(Set.of(
//                "Accept: application/json",
//                "Authorization: Bearer test-token",
//                "Content-Type: application/json",
//                "X-workflow-business-key: user1",
//                "X-workflow-instance-id: process1",
//                "X-workflow-engine-url: http://localhost:9999/engine",
//                "X-workflow-engine-type: camunda",
//                "gsft: gsft",
//                "hello: world"
//        ));
//
//        String content = IoUtil.inputStreamAsString(target.getEntity().getContent());
//
//        assertThat(content).isEqualTo("gsft");
//    }

    private void setMockResponse()
    {
        /* This code mocks the response object for above test cases, as execute method trigger actual rest call
         * This is the approach we have chosen for now.
         */
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);
        StatusLine statusLine = mock(StatusLine.class);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);
        when(response.getAllHeaders()).thenReturn(new Header[0]);
        this.connector.getInterceptor().setResponse(response);
    }
}
