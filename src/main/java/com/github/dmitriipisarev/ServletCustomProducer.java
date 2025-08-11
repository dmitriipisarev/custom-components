package com.github.dmitriipisarev;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServletCustomProducer extends DefaultProducer {
    private static final Logger LOG = LoggerFactory.getLogger(ServletCustomProducer.class);
    private ServletCustomEndpoint endpoint;

    public ServletCustomProducer(ServletCustomEndpoint endpoint) {
        super(endpoint);
        this.endpoint = endpoint;
    }

    public void process(Exchange exchange) throws Exception {
        System.out.println(exchange.getIn().getBody());
    }

}
