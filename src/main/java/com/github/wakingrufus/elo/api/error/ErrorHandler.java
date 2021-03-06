package com.github.wakingrufus.elo.api.error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ErrorHandler implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable ex) {
        int statusCode = (ex instanceof WebApplicationException) ? (((WebApplicationException) ex).getResponse().getStatus()) : 500;
        return Response.status(statusCode)
                .entity(ex.getLocalizedMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}
