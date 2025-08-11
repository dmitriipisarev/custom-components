package com.github.dmitriipisarev;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.github.dmitriipisarev.EventBusHelper;

public class ServletCustomComponentTest extends CamelTestSupport {

    private final EventBusHelper eventBusHelper = EventBusHelper.getInstance();

    @Test
    @Disabled
    public void testServletCustom() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMinimumMessageCount(5);

        // Trigger events to subscribers
        simulateEventTrigger();

        mock.await();
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("servlet-custom://foo")
                  .to("servlet-custom://bar")
                  .to("mock:result");
            }
        };
    }

    private void simulateEventTrigger() {
        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                final Date now = new Date();
                // publish events to the event bus
                eventBusHelper.publish(now);
            }
        };

        new Timer().scheduleAtFixedRate(task, 1000L, 1000L);
    }
}
