package de.bagges.filter;

import de.bagges.filter.shared.Constants;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Created by Markus Backes on 20.07.2015.
 */
@Provider
public class DumpRequestFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(DumpRequestFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("\n-----HTTP REQUEST ---------------------\n");
        builder.append("------------ HTTP REQUEST ------\n");
        builder.append(String.format(Constants.HEADER_TEMPLATE, "Request URL", containerRequestContext.getUriInfo().getAbsolutePath()));
        builder.append(String.format(Constants.HEADER_TEMPLATE, "Request Method", containerRequestContext.getMethod()));
        builder.append("------------ HTTP REQUEST HEADER ------\n");
        containerRequestContext.getHeaders().forEach((k, v) -> builder.append(String.format(Constants.HEADER_TEMPLATE, k, v)));
        builder.append("------------ HTTP REQUEST BODY --------\n");
        if(!containerRequestContext.getMediaType().equals(MediaType.APPLICATION_OCTET_STREAM)) {
            try(Scanner sc = new java.util.Scanner(containerRequestContext.getEntityStream()).useDelimiter("\\A")) {
                if (sc.hasNext()) {
                    String body = sc.next();
                    containerRequestContext.setEntityStream(new ByteArrayInputStream(body.getBytes()));
                    builder.append(body);
                }
            }
        } else {
            builder.append(Constants.SKIPPING_BODY);
        }

        LOG.info(builder.toString());
    }
}
