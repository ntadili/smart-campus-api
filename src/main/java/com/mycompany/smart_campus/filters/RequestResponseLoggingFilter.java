/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 * @author nassertadili
 */
@Provider
public class RequestResponseLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOG =
            Logger.getLogger(RequestResponseLoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LOG.info("--> " + requestContext.getMethod() + " "
                + requestContext.getUriInfo().getRequestUri());
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        LOG.info("<-- " + requestContext.getMethod() + " "
                + requestContext.getUriInfo().getRequestUri()
                + " -> " + responseContext.getStatus());
    }
}
