package io.github.ponchoalfonso.behavior;

import io.github.ponchoalfonso.mqtt.events.SimpleMessageEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SimpleMessageListener implements Listener {

    @EventHandler
    public void onMQTTMessageArrive(SimpleMessageEvent event) {
        Bukkit.broadcastMessage("Incoming message: "+ event.getMessage());
    }
}
