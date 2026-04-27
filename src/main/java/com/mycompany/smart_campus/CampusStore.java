/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart_campus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 *
 * @author nassertadili
 */
public enum CampusStore {
    
    INSTANCE;

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    // ---------- Rooms ----------

    public List<Room> listRooms() {
        return new ArrayList<>(rooms.values());
    }

    public Room findRoom(String roomId) {
        return (roomId == null) ? null : rooms.get(roomId);
    }

    public boolean roomExists(String roomId) {
        return roomId != null && rooms.containsKey(roomId);
    }

    public Room addRoom(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }

    public Room removeRoom(String roomId) {
        return rooms.remove(roomId);
    }

    // ---------- Sensors ----------

    public List<Sensor> listSensors() {
        return new ArrayList<>(sensors.values());
    }

    public Sensor findSensor(String sensorId) {
        return (sensorId == null) ? null : sensors.get(sensorId);
    }

    public Sensor addSensor(Sensor sensor) {
        sensors.put(sensor.getId(), sensor);
        readings.computeIfAbsent(sensor.getId(),
                k -> Collections.synchronizedList(new ArrayList<>()));
        Room room = rooms.get(sensor.getRoomId());
        if (room != null && !room.getSensorIds().contains(sensor.getId())) {
            room.getSensorIds().add(sensor.getId());
        }
        return sensor;
    }

    public Sensor removeSensor(String sensorId) {
        Sensor removed = sensors.remove(sensorId);
        if (removed != null) {
            readings.remove(sensorId);
            Room room = rooms.get(removed.getRoomId());
            if (room != null) {
                room.getSensorIds().remove(sensorId);
            }
        }
        return removed;
    }

    // ---------- Sensor readings ----------

    public List<SensorReading> listReadings(String sensorId) {
        List<SensorReading> log = readings.get(sensorId);
        if (log == null) {
            return new ArrayList<>();
        }
        synchronized (log) {
            return new ArrayList<>(log);
        }
    }

    public SensorReading appendReading(String sensorId, SensorReading reading) {
        List<SensorReading> log = readings.computeIfAbsent(sensorId,
                k -> Collections.synchronizedList(new ArrayList<>()));
        log.add(reading);
        Sensor parent = sensors.get(sensorId);
        if (parent != null) {
            parent.setCurrentValue(reading.getValue());
        }
        return reading;
    }

    /**
     * Wipe all collections. Useful for tests; not exposed via the API.
     */
    public void clear() {
        rooms.clear();
        sensors.clear();
        readings.clear();
    }
}
