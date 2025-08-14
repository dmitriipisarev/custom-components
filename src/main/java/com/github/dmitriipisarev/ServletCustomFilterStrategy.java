package com.github.dmitriipisarev;

import org.apache.camel.Exchange;
import org.apache.camel.http.common.HttpHeaderFilterStrategy;

import java.util.Collection;
import java.util.List;

public class ServletCustomFilterStrategy extends HttpHeaderFilterStrategy {
    private static final Collection<String> FILTERED_HEADERS = List.of(
        "span-id",
        "trace-id",
        "x-requestedsystem"
    );

    public ServletCustomFilterStrategy() {
        this.getOutFilter().addAll(FILTERED_HEADERS);
    }

    @Override
    protected boolean extendedFilter(Direction direction, String headerName, Object headerValue, Exchange exchange) {
        return (Direction.OUT.equals(direction) && isHeaderInContext(headerName)) == isFilterOnMatch();
    }

    private boolean isHeaderInContext(String name) {
        return true;
    }
}
