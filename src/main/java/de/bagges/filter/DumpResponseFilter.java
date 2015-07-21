package de.bagges.filter;

import de.bagges.filter.shared.Constants;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

/**
 * Created by Markus Backes on 20.07.2015.
 */
@Provider
public class DumpResponseFilter implements ContainerResponseFilter, WriterInterceptor {

    private static final Logger LOG = Logger.getLogger(DumpResponseFilter.class.getName());
    private boolean ENABLED = true;
    private StringBuilder builder = new StringBuilder();

    @Context
    private HttpServletRequest request;

    @PostConstruct
    public void init() {
        String enabled = request.getServletContext().getInitParameter(Constants.DUMP_RESPONSE_CONFIG);
        if(enabled != null && !enabled.isEmpty()) {
            ENABLED = enabled.equalsIgnoreCase("TRUE");
        }
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        if(ENABLED) {
            builder = new StringBuilder();
            builder.append("\n-----HTTP RESPONSE ---------------------\n");
            builder.append("------------ HTTP RESPONSE ------\n");
            builder.append(String.format(Constants.HEADER_TEMPLATE, "Status", containerResponseContext.getStatus()));
            builder.append("------------ HTTP RESPONSE HEADER ------\n");
            containerRequestContext.getHeaders().forEach((k, v) -> builder.append(String.format(Constants.HEADER_TEMPLATE, k, v)));
        }
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
        OutputStream originalStream = writerInterceptorContext.getOutputStream();
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            writerInterceptorContext.setOutputStream(byteArrayOutputStream);
            writerInterceptorContext.proceed();
            builder.append("------------ HTTP RESPONSE BODY --------\n");
            builder.append(byteArrayOutputStream.toString("UTF-8"));
            byteArrayOutputStream.writeTo(originalStream);
            writerInterceptorContext.setOutputStream(originalStream);
            LOG.info(builder.toString());
        }
    }
}
