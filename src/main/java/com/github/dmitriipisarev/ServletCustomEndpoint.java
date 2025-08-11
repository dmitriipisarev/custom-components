package com.github.dmitriipisarev;

import org.apache.camel.Category;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.component.servlet.ServletEndpoint;
import org.apache.camel.component.servlet.ServletComponent;
import org.apache.camel.component.servlet.ServletConsumer;
import org.apache.camel.spi.HeaderFilterStrategy;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@UriEndpoint(
        firstVersion = "2.0.0",
        scheme = "servlet-custom",
        extendsScheme = "http",
        title = "Servlet",
        syntax = "servlet-custom:contextPath", consumerOnly = true, category = { Category.HTTP }
)
public class ServletCustomEndpoint extends ServletEndpoint {
    /* @UriParam(label = "advanced", description = "Metric tags provider")
    private ServletTagsProvider tagsProvider; */

    @UriParam(label = "advanced", description = "Unique ID for servlet")
    private String servletCustomId;

    public ServletCustomEndpoint(String endPointURI, ServletComponent component, URI httpUri, HeaderFilterStrategy headerFilterStrategy) throws URISyntaxException {
        super(endPointURI, component, httpUri);

        // Allow to use non-unique URI during redeploy
        this.servletCustomId = UUID.randomUUID().toString();
        super.setEndpointUri(endPointURI + "&servletCustomId=" + this.servletCustomId);
        super.setHttpUri(new URIBuilder(httpUri).addParameter("servletCustomId", this.servletCustomId).build());
        super.setHeaderFilterStrategy(headerFilterStrategy);
    }

    /* public ServletTagsProvider getTagsProvider() {
        return tagsProvider;
    }

    public void setTagsProvider(ServletTagsProvider tagsProvider) {
        this.tagsProvider = tagsProvider;
    } */

    public String getServletCustomId() {
        return servletCustomId;
    }

    public void setServletCustomId(String servletCustomId) {
        // do nothing, generated automatically
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        ServletConsumer answer = new ServletCustomConsumer(this, processor);
        configureConsumer(answer);
        return answer;
    }
}
