/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus;

import com.mycompany.smart_campus.filters.RequestResponseLoggingFilter;
import com.mycompany.smart_campus.mappers.GenericExceptionMapper;
import com.mycompany.smart_campus.mappers.LinkedResourceNotFoundExceptionMapper;
import com.mycompany.smart_campus.mappers.RoomNotEmptyExceptionMapper;
import com.mycompany.smart_campus.mappers.SensorUnavailableExceptionMapper;
import com.mycompany.smart_campus.resources.DiscoveryResource;
import com.mycompany.smart_campus.resources.RoomResource;
import com.mycompany.smart_campus.resources.SensorResource;

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
        classes.add(SensorResource.class);

        // Exception mappers
        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(GenericExceptionMapper.class);

        // Filters
        classes.add(RequestResponseLoggingFilter.class);

        return classes;
    }
}
