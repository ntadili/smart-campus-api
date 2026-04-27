/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus;

/**
 *
 * @author nassertadili
 */
public class Room {
    private String id; // Unique identifier , e.g., "LIB -301"
    private String name ; // Human - readable name , e.g., " Library Quiet Study "
    private int capacity ; // Maximum occupancy for safety regulations
    private List<String> sensorIds = new ArrayList<>(); //Collections of IDs of sensors deployed in this room
    
    // Constructors, getters and setters...
}
