/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus;

import com.mycompany.smart_campus.resources.DiscoveryResource;
import com.mycompany.smart_campus.resources.RoomResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author nassertadili
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application{
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Resources
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);

        // Exception mappers (added in Part 5)
        // classes.add(...);

        // Filters (added in Part 5)
        // classes.add(...);

        return classes;
    }
}
