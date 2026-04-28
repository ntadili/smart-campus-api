/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus.resources;

import com.mycompany.smart_campus.CampusStore;
import com.mycompany.smart_campus.Sensor;
import com.mycompany.smart_campus.SensorReading;
import com.mycompany.smart_campus.exceptions.SensorUnavailableException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;


/**
 *
 * @author nassertadili
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
   
    private final CampusStore store = CampusStore.INSTANCE;
    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public Response listReadings() {
        if (store.findSensor(sensorId) == null) {
            return sensorNotFound();
        }
        return Response.ok(store.listReadings(sensorId)).build();
    }

    @POST
    public Response appendReading(SensorReading incoming, @Context UriInfo uriInfo) {
        Sensor sensor = store.findSensor(sensorId);
        if (sensor == null) {
            return sensorNotFound();
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(sensorId, sensor.getStatus());
        }
        if (incoming == null) {
            return badRequest("Request body must be a JSON SensorReading object.");
        }

        if (incoming.getId() == null || incoming.getId().isBlank()) {
            incoming.setId(UUID.randomUUID().toString());
        }
        if (incoming.getTimestamp() <= 0L) {
            incoming.setTimestamp(System.currentTimeMillis());
        }

        SensorReading saved = store.appendReading(sensorId, incoming);

        URI location = uriInfo.getAbsolutePathBuilder().path(saved.getId()).build();
        return Response.created(location).entity(saved).build();
    }


    private Response sensorNotFound() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "SENSOR_NOT_FOUND");
        body.put("message", "No sensor exists with id '" + sensorId + "'.");
        return Response.status(Response.Status.NOT_FOUND).entity(body).build();
    }

    private static Response badRequest(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "BAD_REQUEST");
        body.put("message", message);
        return Response.status(Response.Status.BAD_REQUEST).entity(body).build();
    } 
}
