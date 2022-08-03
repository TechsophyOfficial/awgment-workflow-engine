package com.techsophy.tsf.workflow.engine.camunda.config;

import lombok.AllArgsConstructor;
import org.camunda.connect.Connectors;
import org.camunda.connect.spi.Connector;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.List;

/**
 * used to initialize connectors in camunda
 */
@Component
@AllArgsConstructor
public class ConnectorsInitializer extends Connectors
{
    private final List<Connector<?>> connectors;

    @PostConstruct
    public void initialize()
    {
        this.registerSpringDiscoveredConnectors();
    }

    /**
     * register connectors
     */
    private void registerSpringDiscoveredConnectors()
    {
        this.connectors.forEach(Connectors::registerConnector);
    }
}
