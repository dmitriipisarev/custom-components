package com.github.dmitriipisarev;

import com.arakelian.json.ImmutableJsonFilterOptions;
import com.arakelian.json.JsonFilter;
import com.arakelian.json.JsonReader;
import com.arakelian.json.JsonWriter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.http.common.DefaultHttpBinding;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

@Slf4j
public class HandlingHttpBinding extends DefaultHttpBinding {
    public HandlingHttpBinding(ServletCustomFilterStrategy servletCustomFilterStrategy) {
        super();
        setHeaderFilterStrategy(servletCustomFilterStrategy);
    }

    @Override
    public void writeResponse(Exchange exchange, HttpServletResponse response) throws IOException {
        Message target = exchange.getMessage();
        if (exchange.isFailed()) {
            if (exchange.getException() != null) {
                doWriteExceptionResponse(exchange.getException(), response, exchange);
            } else {
                // it must be a fault, no need to check for the fault flag on the message
                doWriteFaultResponse(target, response, exchange);
            }
        } else {
            if (exchange.hasOut()) {
                // just copy the protocol relates header if we do not have them
                copyProtocolHeaders(exchange.getIn(), exchange.getOut());
            }
            doWriteResponse(target, response, exchange);
        }
    }

    private void copyProtocolHeaders(Message request, Message response) {
        if (request.getHeader(Exchange.CONTENT_ENCODING) != null) {
            String contentEncoding = request.getHeader(Exchange.CONTENT_ENCODING, String.class);
            response.setHeader(Exchange.CONTENT_ENCODING, contentEncoding);
        }
        if (checkChunked(response, response.getExchange())) {
            response.setHeader(Exchange.TRANSFER_ENCODING, "chunked");
        }
    }

    @Override
    public void doWriteExceptionResponse(Throwable exception, HttpServletResponse response) throws IOException {
        doWriteExceptionResponse(exception, response, null);
    }

    public void doWriteExceptionResponse(Throwable exception, HttpServletResponse response, Exchange exchange) throws IOException {
        if (exchange != null) {
            super.doWriteResponse(exchange.getMessage(), response, exchange);
            return;
        }

        sendInternalException(exception, response);
    }

    @Override
    public void doWriteResponse(Message message, HttpServletResponse response, Exchange exchange) throws IOException {
        if (!exchange.isFailed()) {
            filterFieldsInResponse(message, exchange);
        }
        super.doWriteResponse(message, response, exchange);
    }

    private void sendInternalException(Throwable e, HttpServletResponse response) throws IOException {
        log.error("Unable to respond from chain http trigger due to exception", e);
        response.sendError(500);
    }

    private void filterFieldsInResponse(Message message, Exchange exchange) {
        String filterIncludeFields = exchange.getProperty("internalProperty_responseFilterExclude", String.class);
        String filterExcludeFields = exchange.getProperty("internalProperty_responseFilterInclude", String.class);

        ImmutableJsonFilterOptions.Builder filterOptionsBuilder = null;
        if (filterIncludeFields != null) {
            filterOptionsBuilder = getJsonFilterBuilder(filterOptionsBuilder)
                    .addAllIncludes(parseResponseFilterFields(filterIncludeFields));
        }
        if (filterExcludeFields != null) {
            filterOptionsBuilder = getJsonFilterBuilder(filterOptionsBuilder)
                    .addAllExcludes(parseResponseFilterFields(filterExcludeFields));
        }

        if (filterOptionsBuilder != null) {
            StringReader sr;
            StringWriter sw;
            try {
                String body = message.getBody(String.class);
                message.setBody(body);
                sr = new StringReader(body);
                sw = new StringWriter();
            } catch (Exception e) {
                log.warn("Unable to convert body for response filter", e);
                return;
            }
            JsonFilter filter = new JsonFilter(new JsonReader(sr), new JsonWriter(sw), filterOptionsBuilder.build());
            try {
                filter.process();
                message.setBody(sw.toString());
            } catch (IOException e) {
                log.warn("Failed to filter response", e);
                return;
            }
        }
    }

    private ImmutableJsonFilterOptions.Builder getJsonFilterBuilder(ImmutableJsonFilterOptions.Builder filterOptionsBuilder) {
        return filterOptionsBuilder == null ? ImmutableJsonFilterOptions.builder() : filterOptionsBuilder;
    }

    private List<String> parseResponseFilterFields(String filterFields) {
        return List.of(StringUtils.stripAll(filterFields.replace('.', '/').split(",")));
    }
}
