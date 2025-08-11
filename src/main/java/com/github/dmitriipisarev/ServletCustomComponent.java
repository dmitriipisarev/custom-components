package com.github.dmitriipisarev;

import java.net.URI;
import org.apache.camel.component.servlet.ServletComponent;
import org.apache.camel.component.servlet.ServletEndpoint;
import org.apache.camel.spi.annotations.Component;

@Component("servlet-custom")
public class ServletCustomComponent extends ServletComponent {

    @Override
    protected ServletEndpoint createServletEndpoint(String endpointUri, ServletComponent component, URI httpUri) throws Exception {
        return new ServletCustomEndpoint(endpointUri, component, httpUri, getHeaderFilterStrategy());
    }
}
