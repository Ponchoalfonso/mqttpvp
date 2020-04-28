package io.github.ponchoalfonso.mqtt.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SensorMessageEvent extends Event {
    private final String sensorName;
    private final String message;

    public SensorMessageEvent(String sensorName, String message) {
        this.message = message;
        this.sensorName = sensorName;
    }

    private static final HandlerList HANDLERS = new HandlerList();
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public String getSensorName() { return this.sensorName; }
    public String getMessage() {
        return this.message;
    }
}
