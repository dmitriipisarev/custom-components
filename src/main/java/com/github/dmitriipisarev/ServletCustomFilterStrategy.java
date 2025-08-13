package com.github.dmitriipisarev;

import org.apache.camel.Exchange;
import org.apache.camel.http.common.HttpHeaderFilterStrategy;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

@ApplicationScoped
public class ServletCustomFilterStrategy extends HttpHeaderFilterStrategy {
    private static final Collection<String> FILTERED_HEADERS = List.of(
        "span-id",
        "trace-id",
        "x-requestedsystem"
    );

    private final Optional<ContextPropsProvider> contextPropsProvider;

    public ServletCustomFilterStrategy(Optional<ContextPropsProvider> contextPropsProvider) {
        this.contextPropsProvider = contextPropsProvider;
        this.getOutFilter().addAll(FILTERED_HEADERS);
    }

    @Override
    protected boolean extendedFilter(Direction direction, String headerName, Object headerValue, Exchange exchange) {
        return (Direction.OUT.equals(direction) && isHeaderInContext(headerName)) == isFilterOnMatch();
    }

    private boolean isHeaderInContext(String name) {
        return this.contextPropsProvider
            .map(ContextPropsProvider::getDownstreamHeaders)
            .map(headers -> nonNull(headers) && headers.contains(name))
            .orElse(false);
    }
}
