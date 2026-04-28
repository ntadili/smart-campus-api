/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus.resources;

import com.mycompany.smart_campus.CampusStore;
import com.mycompany.smart_campus.Sensor;
import com.mycompany.smart_campus.exceptions.LinkedResourceNotFoundException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author nassertadili
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    
    private final CampusStore store = CampusStore.INSTANCE;

    @GET
    public List<Sensor> listSensors(@QueryParam("type") String type) {
        List<Sensor> all = store.listSensors();
        if (type == null || type.isBlank()) {
            return all;
        }
        List<Sensor> filtered = new ArrayList<>();
        for (Sensor s : all) {
            if (s.getType() != null && s.getType().equalsIgnoreCase(type)) {
                filtered.add(s);
            }
        }
        return filtered;
    }


    @POST
    public Response createSensor(Sensor incoming, @Context UriInfo uriInfo) {
        if (incoming == null) {
            return badRequest("BAD_REQUEST",
                    "Request body must be a JSON Sensor object.");
        }
        if (incoming.getRoomId() == null || incoming.getRoomId().isBlank()) {
            return badRequest("BAD_REQUEST",
                    "Field 'roomId' is required when creating a sensor.");
        }
        if (!store.roomExists(incoming.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room", incoming.getRoomId());
        }

        if (incoming.getId() == null || incoming.getId().isBlank()) {
            incoming.setId(UUID.randomUUID().toString());
        } else if (store.findSensor(incoming.getId()) != null) {
            return conflict("A sensor with id '" + incoming.getId()
                    + "' already exists.");
        }

        Sensor saved = store.addSensor(incoming);
        URI location = uriInfo.getAbsolutePathBuilder().path(saved.getId()).build();
        return Response.created(location).entity(saved).build();
    }
    
    
    /**
     * Sub-resource locator for /api/v1/sensors/{sensorId}/readings.
     *
     * This method has NO HTTP verb annotation. JAX-RS treats it as a
     * locator: it returns a {@link SensorReadingResource} instance and
     * delegates the rest of the request matching (GET, POST, etc.) to
     * that object. 
     */
    @Path("{sensorId}/readings")
    public SensorReadingResource readings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
    
    

    // ---------- Local error helpers (Part 5 will centralise these) ----------

    private static Response badRequest(String errorCode, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", errorCode);
        body.put("message", message);
        return Response.status(Response.Status.BAD_REQUEST).entity(body).build();
    }

    private static Response conflict(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "SENSOR_CONFLICT");
        body.put("message", message);
        return Response.status(Response.Status.CONFLICT).entity(body).build();
    }
}
