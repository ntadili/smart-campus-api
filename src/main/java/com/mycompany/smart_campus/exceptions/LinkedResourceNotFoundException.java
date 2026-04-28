/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus.exceptions;

/**
 *
 * @author nassertadili
 */
public class LinkedResourceNotFoundException extends RuntimeException {
    
    private final String resourceType;
    private final String missingId;

    public LinkedResourceNotFoundException(String resourceType, String missingId) {
        super("Referenced " + resourceType + " with id '" + missingId
                + "' does not exist.");
        this.resourceType = resourceType;
        this.missingId = missingId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getMissingId() {
        return missingId;
    }
}
