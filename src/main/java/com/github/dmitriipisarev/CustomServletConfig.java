package com.github.dmitriipisarev;

import org.apache.camel.spi.ComponentCustomizer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class CustomServletConfig {
    @Produces
    @ApplicationScoped
    public ComponentCustomizer servletCustomComponentCustomizer() {
        return ComponentCustomizer.builder(ServletCustomComponent.class)
                .build((component) -> {
                    component.setHeaderFilterStrategy(new ServletCustomFilterStrategy());
                });
    }
}
