package io.github.ponchoalfonso.mqtt;

import io.github.ponchoalfonso.MQTTPvP;
import io.github.ponchoalfonso.mqtt.events.SensorMessageEvent;
import io.github.ponchoalfonso.mqtt.events.SimpleMessageEvent;

import org.bukkit.Bukkit;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static java.lang.Thread.sleep;

public class MQTTClient implements MqttCallback {
    private MqttAsyncClient client;
    private MQTTPvP plugin;

    public MQTTClient(String brokerURL, MQTTPvP plugin) {
        this.plugin = plugin;
        try {
            client = new MqttAsyncClient(brokerURL, "Minecraft Server", null);
            client.setCallback(this);
        } catch (Exception e) {
            Bukkit.getLogger().warning("MQTT Client couldn't be initialized!");
        }
    }

    public void start() {
        if (client != null) {
            Bukkit.getLogger().info("Client initialized correctly!");
            try {
                client.connect();
                sleep(1000);
            } catch (Exception e) {
                Bukkit.getLogger().warning("MQTT Client couldn't connect with broker!");
            }
            if (client.isConnected()) {
                Bukkit.getLogger().info("MQTT Client is now online!");
                try {
                    client.subscribe("minecraft", 1);
                    client.subscribe("minecraft/sensors/#", 1);
                    Bukkit.getLogger().info("Subscribed to all sensors topics correctly!");
                } catch (Exception e) {
                    Bukkit.getLogger().warning("MQTT Client: Something went wrong while subscribing to topics!");
                }
            }
        }
    }

    public void stop() {
        if (client != null) {
            if (client.isConnected()) {
                try {
                    client.disconnect();
                    sleep(1000);
                } catch (Exception e) {
                    Bukkit.getLogger().warning("Something went wrong while disconnecting the MQTT Client!");
                }
            } else {
                Bukkit.getLogger().warning("MQTT Client already offline!");
            }
            try {
                client.close();
            } catch (Exception e) {
                Bukkit.getLogger().warning("MQTT Client: Something went wrong while closing the client!");
            }
        }
    }


    @Override
    public void connectionLost(Throwable throwable) {
        Bukkit.getLogger().warning("MQTT Client: Connection lost!");
        Bukkit.getLogger().warning("Reason: "+ throwable.getCause().getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String[] st = topic.split("/");
        String message = mqttMessage.toString();

        if (topic.equals("minecraft")) {
            final SimpleMessageEvent messageEvent = new SimpleMessageEvent(message);
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(messageEvent);
                }
            });
        } else if (st.length > 2 && st[1].equals("sensors")) {
            String sensorName = st[2];
            final SensorMessageEvent sensorEvent = new SensorMessageEvent(sensorName, message);
            Bukkit.getScheduler().runTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Bukkit.getPluginManager().callEvent(sensorEvent);
                }
            });
        } else {
            Bukkit.getLogger().warning("MQTT Client: Unrecognized topic. Possible unauthorized sender!");
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // Do nothing!
        // This client only receives
    }
}
