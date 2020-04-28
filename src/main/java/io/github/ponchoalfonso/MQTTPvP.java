/*
 * MQTTPvP Plugin - by Alfonso Valencia
 *
 * Notes for my developer fellas!
 * NOTE: The Minecraft server measures time in ticks: 20ticks = 1s
 */
package io.github.ponchoalfonso;

import io.github.ponchoalfonso.behavior.SensorMessageListener;
import io.github.ponchoalfonso.behavior.SimpleMessageListener;
import io.github.ponchoalfonso.mqtt.CommandMQTT;
import io.github.ponchoalfonso.mqtt.MQTTClient;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * <h1>Class MQTTPvP</h1>
 * The main plugin class.<br>
 * The class where you register events, listeners, commands and some extra logic.
 *
 * @author Alfonso Valencia
 * @version 1.0
 */
public class MQTTPvP extends JavaPlugin {

    private MQTTClient mqttClient;

    @Override
    public void onEnable() {
        // Listeners
        getServer().getPluginManager().registerEvents(new SimpleMessageListener(), this);
        getServer().getPluginManager().registerEvents(new SensorMessageListener(this), this);

        // Commands
        this.getCommand("mqtt").setExecutor(new CommandMQTT());

        // Extra logic
        mqttClient = new MQTTClient("tcp://localhost:1883", this);
        mqttClient.start();

        // Load message
        getLogger().info("MQTT PvP Plugin is now ready to go!");
    }
    @Override
    public void onDisable() {
        mqttClient.stop();
        getLogger().info("Everything is OK, ready to shutdown!");
    }
}
