/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus.mappers;

import com.mycompany.smart_campus.exceptions.LinkedResourceNotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author nassertadili
 */
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    /** RFC 4918 status code for "Unprocessable Entity". */
    private static final int UNPROCESSABLE_ENTITY = 422;

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "LINKED_RESOURCE_NOT_FOUND");
        body.put("message", ex.getMessage());
        body.put("resourceType", ex.getResourceType());
        body.put("missingId", ex.getMissingId());
        return Response.status(UNPROCESSABLE_ENTITY)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();
    }
}
