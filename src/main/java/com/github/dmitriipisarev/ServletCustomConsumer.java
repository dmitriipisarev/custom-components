package com.github.dmitriipisarev;

import org.apache.camel.Processor;
import org.apache.camel.component.servlet.ServletConsumer;
import org.apache.camel.component.servlet.ServletEndpoint;
import java.util.Date;

public class ServletCustomConsumer extends ServletConsumer {
    private final long creationTime;

    public ServletCustomConsumer(ServletEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.creationTime = new Date().getTime();
    }

    public long getCreationTime() {
        return creationTime;
    }
}
