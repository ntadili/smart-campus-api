/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus;

/**
 *
 * @author nassertadili
 */
public class Sensor {
    private String id; // Unique identifier , e.g., "TEMP -001"
    private String type ; // Category , e.g., " Temperature ", "Occupancy ", "CO2"
    private String status ; // Current state : " ACTIVE ", "MAINTENANCE ", or " OFFLINE "
    private double currentValue ; // The most recent measurement recorded
    private String roomId ; // Foreign key linking to the Room where the sensor is located .
    
    // Constructors , getters , setters ...
}
