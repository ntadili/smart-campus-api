/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus.resources;

import com.mycompany.smart_campus.CampusStore;
import com.mycompany.smart_campus.Room;
import com.mycompany.smart_campus.exceptions.RoomNotEmptyException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author nassertadili
 *  * Room resource (Part 2).
 *
 * Implements the four endpoints required:
 *   GET    /api/v1/rooms          - list all rooms
 *   POST   /api/v1/rooms          - create a new room
 *   GET    /api/v1/rooms/{roomId} - fetch one room's metadata
 *   DELETE /api/v1/rooms/{roomId} - decommission a room (blocked when
 *                                   the room still has sensors assigned)
 */

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {
    
    private final CampusStore store = CampusStore.INSTANCE;

    @GET
    public List<Room> listRooms() {
        return store.listRooms();
    }

    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = store.findRoom(roomId);
        if (room == null) {
            return notFound(roomId);
        }
        return Response.ok(room).build();
    }

    @POST
    public Response createRoom(Room incoming, @Context UriInfo uriInfo) {
        if (incoming == null) {
            return badRequest("Request body must be a JSON Room object.");
        }

        if (incoming.getId() == null || incoming.getId().isBlank()) {
            incoming.setId(UUID.randomUUID().toString());
        } else if (store.roomExists(incoming.getId())) {
            return conflict("A room with id '" + incoming.getId() + "' already exists.");
        }

        Room saved = store.addRoom(incoming);
        URI location = uriInfo.getAbsolutePathBuilder().path(saved.getId()).build();
        return Response.created(location).entity(saved).build();
    }
    
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.findRoom(roomId);
        if (room == null) {
            return notFound(roomId);
        }
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(roomId, room.getSensorIds().size());
        }
        store.removeRoom(roomId);
        return Response.noContent().build();
    }

    // ---------- Local error helpers (Part 5 will centralise these) ----------

    private static Response notFound(String roomId) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "ROOM_NOT_FOUND");
        body.put("message", "No room exists with id '" + roomId + "'.");
        return Response.status(Response.Status.NOT_FOUND).entity(body).build();
    }

    private static Response conflict(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "ROOM_CONFLICT");
        body.put("message", message);
        return Response.status(Response.Status.CONFLICT).entity(body).build();
    }

    private static Response badRequest(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "BAD_REQUEST");
        body.put("message", message);
        return Response.status(Response.Status.BAD_REQUEST).entity(body).build();
    }  
}
