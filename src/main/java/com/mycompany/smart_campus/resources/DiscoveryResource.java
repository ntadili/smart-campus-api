/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author nassertadili
 */
@Path("/")
public class DiscoveryResource {
    
    private static final String API_NAME = "Smart Campus Sensor & Room Management API";
    private static final String API_VERSION = "1.0.0";
    private static final String API_RELEASED = "2026-04-27";
    private static final String API_DESCRIPTION =
            "JAX-RS RESTful service for managing rooms, sensors and sensor readings " + "on a smart campus.";
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> root(@Context UriInfo uriInfo) {
        URI selfUri = uriInfo.getRequestUriBuilder().build();
        URI roomsUri = uriInfo.getBaseUriBuilder().path("rooms").build();
        URI sensorsUri = uriInfo.getBaseUriBuilder().path("sensors").build();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("api", API_NAME);
        body.put("version", API_VERSION);
        body.put("releaseDate", API_RELEASED);
        body.put("description", API_DESCRIPTION);
        body.put("contact", buildContact());
        body.put("resources", buildResourceMap(roomsUri, sensorsUri));
        body.put("_links", buildLinks(selfUri, roomsUri, sensorsUri));
        return body;
    }

    private Map<String, Object> buildContact() {
        Map<String, Object> contact = new LinkedHashMap<>();
        contact.put("name", "Smart Campus Backend Team");
        contact.put("email", "admin@smartcampus.ac.uk");
        contact.put("organisation", "Smart Campus University");
        return contact;
    }

    private Map<String, Object> buildResourceMap(URI roomsUri, URI sensorsUri) {
        Map<String, Object> resources = new LinkedHashMap<>();
        resources.put("rooms", roomsUri.toString());
        resources.put("sensors", sensorsUri.toString());
        return resources;
    }

    private Map<String, Object> buildLinks(URI selfUri, URI roomsUri, URI sensorsUri) {
        Map<String, Object> links = new LinkedHashMap<>();
        links.put("self", link(selfUri, "GET"));
        links.put("rooms", link(roomsUri, "GET, POST"));
        links.put("sensors", link(sensorsUri, "GET, POST"));
        return links;
    }

    private Map<String, Object> link(URI href, String methods) {
        Map<String, Object> link = new LinkedHashMap<>();
        link.put("href", href.toString());
        link.put("methods", methods);
        return link;
    }
}
