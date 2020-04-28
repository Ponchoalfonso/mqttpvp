package io.github.ponchoalfonso.mqtt.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SimpleMessageEvent extends Event {
    private final String message;

    public SimpleMessageEvent(String message) {
        this.message = message;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public String getMessage() {
        return this.message;
    }
}
