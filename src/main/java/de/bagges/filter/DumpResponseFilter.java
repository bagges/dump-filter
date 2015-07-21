package de.bagges.filter;

import de.bagges.filter.shared.Constants;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Markus Backes on 20.07.2015.
 */
@Provider
public class DumpResponseFilter implements ContainerResponseFilter {

    private static final Logger LOG = Logger.getLogger(DumpResponseFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("\n-----HTTP RESPONSE ---------------------\n");
        builder.append("------------ HTTP RESPONSE ------\n");
        builder.append(String.format(Constants.HEADER_TEMPLATE, "Status", containerResponseContext.getStatus()));
        builder.append("------------ HTTP RESPONSE HEADER ------\n");
        containerRequestContext.getHeaders().forEach((k, v) -> builder.append(String.format(Constants.HEADER_TEMPLATE, k, v)));
        builder.append("------------ HTTP RESPONSE BODY --------\n");
        if(!containerResponseContext.getMediaType().equals(MediaType.APPLICATION_OCTET_STREAM)) {
            builder.append(containerResponseContext.getEntity());
        } else {
            builder.append(Constants.SKIPPING_BODY);
        }
        LOG.info(builder.toString());
    }
}
